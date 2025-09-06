package io.github.su322.fastexceldemo.service.impl;

import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.su322.fastexceldemo.repository.entity.CommentDO;
import io.github.su322.fastexceldemo.repository.mapper.CommentMapper;
import io.github.su322.fastexceldemo.service.CommentService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    // 分sheet 分页查询写入
    public void download2excel(HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis(); // 统计导出开始时间
        int pageSize = 10000; // 每页1万条
        int sheetSize = 1000000; // 人工设置的Excel单sheet最大写入行数
        long total = countComments();
        int sheetCount = (int) (total / sheetSize + (total % sheetSize == 0 ? 0 : 1)); // 需要的sheet数

        try (ExcelWriter excelWriter = FastExcel.write(response.getOutputStream(), CommentDO.class).build()) {
            // 外层循环sheet，内层循环分页，动态计算每个sheet实际需要写入的数据量，防止超出Excel单sheet最大行数 之前为什么报错我还没搞懂
            for (int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
                // 创建当前sheet
                WriteSheet sheet = FastExcel.writerSheet(sheetIndex, "sheet" + (sheetIndex + 1)).build();
                long sheetStart = sheetIndex * (long) sheetSize;
                long sheetEnd = Math.min(sheetStart + sheetSize, total);
                long sheetRowCount = sheetEnd - sheetStart; // 动态计算每个sheet实际需要写入的数据量
                long written = 0; // 当前sheet已写入行数
                long pageNum = sheetStart / pageSize + 1; // 当前sheet的起始页码
                while (written < sheetRowCount) {
                    Page<CommentDO> page = new Page<>(pageNum, pageSize);
                    List<CommentDO> data = commentMapper.selectPage(page, null).getRecords();
                    // 理论上不会触发，仅作健壮性防御，防止极端情况下分页查不到数据导致死循环
                    if (data.isEmpty()) break;
                    long remain = sheetRowCount - written;
                    // 最后一页只写剩余数据，防止超行 数据不会少，因为本来该写多少就是多少��但是数据是否一致呢
                    if (data.size() > remain) {
                        data = data.subList(0, (int) remain);
                    }
                    excelWriter.write(data, sheet);
                    written += data.size();
                    pageNum++;
                }
            }
        } finally {
            long end = System.currentTimeMillis(); // 统计导出结束时间
            System.out.println("导出耗时: " + (end - start) + " ms"); // 216206ms
        }
    }

    /**
     * 并发分页查询+串行写入Excel，4线程导出
     */
    @Override
    public void download2excelConcurrent(HttpServletResponse response) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        int pageSize = 10000;
        int sheetSize = 1000000;
        long total = countComments();
        int sheetCount = (int) (total / sheetSize + (total % sheetSize == 0 ? 0 : 1));
        int threadCount = 4; // mac:10 服务器:4 结果和虚拟线程差不多

        try (ExcelWriter excelWriter = FastExcel.write(response.getOutputStream(), CommentDO.class).build()) {
            for (int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
                WriteSheet sheet = FastExcel.writerSheet(sheetIndex, "sheet" + (sheetIndex + 1)).build();
                long sheetStart = sheetIndex * (long) sheetSize;
                long sheetEnd = Math.min(sheetStart + sheetSize, total);
                long sheetRowCount = sheetEnd - sheetStart;
                int pageCount = (int) (sheetRowCount / pageSize + (sheetRowCount % pageSize == 0 ? 0 : 1));

                try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
                    CountDownLatch latch = new CountDownLatch(pageCount);
                    ConcurrentHashMap<Integer, List<CommentDO>> pageDataMap = new ConcurrentHashMap<>();

                    for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                        final int idx = pageIndex;
                        final long pageNum = sheetStart / pageSize + idx + 1;
                        executor.submit(() -> {
                            try {
                                Page<CommentDO> page = new Page<>(pageNum, pageSize);
                                List<CommentDO> data = commentMapper.selectPage(page, null).getRecords();
                                pageDataMap.put(idx, data);
                            } finally {
                                latch.countDown();
                            }
                        });
                    }
                    latch.await();

                    long written = 0;
                    for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                        List<CommentDO> data = pageDataMap.get(pageIndex);
                        if (data == null || data.isEmpty()) break;
                        long remain = sheetRowCount - written;
                        if (data.size() > remain) {
                            data = data.subList(0, (int) remain);
                        }
                        excelWriter.write(data, sheet);
                        written += data.size();
                    }
                } // try-with-resources 自动关闭 executor
            }
        } finally {
            long end = System.currentTimeMillis();
            System.out.println("并发导出耗时: " + (end - start) + " ms"); // 78651ms
        }
    }

    @Override
    // 你的 VirtualThreadExample 只做了极其简单的任务（int x = 1 + 1;），没有任何 I/O、没有阻塞、没有实际业务逻辑。主要消耗在于线程的创建和销毁。
    // 所以在这种“纯线程创建/销毁”场景下，虚拟线程的优势被极大放大。
    // 真实业务（如数据库分页导出）主要耗时在I/O操作（数据库、网络、磁盘），而不是线程的创建和销毁。
    // 只要线程池/虚拟线程数量足够，I/O密集型任务的总耗时主要受限于外部系统（数据库、磁盘等），而不是线程调度。
    // 这时，虚拟线程和线程池的差距就被“业务瓶颈”掩盖了。
    public void download2excelVirtualThread(HttpServletResponse response) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        int pageSize = 10000;
        int sheetSize = 1000000;
        long total = countComments();
        int sheetCount = (int) (total / sheetSize + (total % sheetSize == 0 ? 0 : 1));

        try (ExcelWriter excelWriter = FastExcel.write(response.getOutputStream(), CommentDO.class).build()) {
            for (int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
                WriteSheet sheet = FastExcel.writerSheet(sheetIndex, "sheet" + (sheetIndex + 1)).build();
                long sheetStart = sheetIndex * (long) sheetSize;
                long sheetEnd = Math.min(sheetStart + sheetSize, total);
                long sheetRowCount = sheetEnd - sheetStart;
                int pageCount = (int) (sheetRowCount / pageSize + (sheetRowCount % pageSize == 0 ? 0 : 1));

                try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    CountDownLatch latch = new CountDownLatch(pageCount);
                    ConcurrentHashMap<Integer, List<CommentDO>> pageDataMap = new ConcurrentHashMap<>();

                    for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                        final int idx = pageIndex;
                        final long pageNum = sheetStart / pageSize + idx + 1;
                        executor.submit(() -> {
                            try {
                                Page<CommentDO> page = new Page<>(pageNum, pageSize);
                                List<CommentDO> data = commentMapper.selectPage(page, null).getRecords();
                                pageDataMap.put(idx, data);
                            } finally {
                                latch.countDown();
                            }
                        });
                    }
                    latch.await();

                    long written = 0;
                    for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                        List<CommentDO> data = pageDataMap.get(pageIndex);
                        if (data == null || data.isEmpty()) break;
                        long remain = sheetRowCount - written;
                        if (data.size() > remain) {
                            data = data.subList(0, (int) remain);
                        }
                        excelWriter.write(data, sheet);
                        written += data.size();
                    }
                }
            }
        } finally {
            long end = System.currentTimeMillis();
            System.out.println("虚拟线程并发导出耗时: " + (end - start) + " ms"); // 76721ms
        }

    }

    @Override
    // https://cloud.tencent.com/developer/article/2271787 这里说的，我最开始用的是最慢的方式，现在确实快了很多 mac:155.81s
    // 开启批处理，关闭自动提交事务，共用同一个SqlSession之后，for循环单条插入的性能得到实质性的提高；
    // 由于同一个SqlSession省去对资源相关操作的耗能、减少对事务处理的时间等，从而极大程度上提高执行效率。
    public void batchInsert5Million() {
        long start = System.currentTimeMillis(); // 统计插入开始时间
        int total = 5_000_000;
        int batchSize = 10000;
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try (session) {
            // 批处理/手动事务时，必须用 session.getMapper 获取 Mapper，确保所有操作都在同一个 SqlSession 上下文中。
            // 直接用 @Autowired 的 Mapper 只适合普通单条操作，不适合批处理。不懂
            CommentMapper mapper = session.getMapper(CommentMapper.class);
            for (int i = 0; i < total; i++) {
                CommentDO comment = CommentDO.builder()
                        .userId(1L)
                        .content("测试评��内容" + (i + 1)) // 这个可以看顺序
                        .createTime(LocalDateTime.now())
                        .build();
                mapper.insert(comment);
                // 每插入 batchSize 条数据就提交一次事务（session.commit()），并清理缓存（session.clearCache()）。
                // 这样做的目的是防止内存溢出，并提升批量插入性��。我信你吧
                if ((i + 1) % batchSize == 0) {
                    session.commit();
                    session.clearCache();
                }
            }
            session.commit();
        }
        long end = System.currentTimeMillis(); // 统计插入结束时间
        System.out.println("插入耗时: " + (end - start) + " ms");
    }

    @Override
    public void deleteAllComments() {
        commentMapper.delete(null);
    }

    @Override
    public long countComments() {
        return commentMapper.selectCount(null);
    }

}
