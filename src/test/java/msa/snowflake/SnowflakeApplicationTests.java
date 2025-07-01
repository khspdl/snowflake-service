package msa.snowflake;

import msa.snowflake.service.SnowflakeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SnowflakeApplicationTests {

    @Autowired
    SnowflakeService snowflakeService;

    @Test
    void getIdTest() throws ExecutionException, InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<List<Long>>> futures = new ArrayList<>();
        int repeatCount = 1000;
        int idCount = 1000;

        // when
        for (int i = 0; i < repeatCount; i++) {
            futures.add(executorService.submit(() -> generateIdList(snowflakeService, idCount)));
        }

        // then
        List<Long> result = new ArrayList<>();
        for (Future<List<Long>> future : futures) {
            List<Long> idList = future.get();
            for (int i = 1; i < idList.size(); i++) {
                assertThat(idList.get(i)).isGreaterThan(idList.get(i - 1));
            }
            result.addAll(idList);
        }
        assertThat(result.stream().distinct().count()).isEqualTo(repeatCount * idCount);

        executorService.shutdown();
    }

    List<Long> generateIdList(SnowflakeService snowflakeService, int count) {
        List<Long> idList = new ArrayList<>();
        while (count-- > 0) {
            idList.add(snowflakeService.getId());
        }
        return idList;
    }

    @Test
    void nextIdPerformanceTest() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int repeatCount = 1000;
        int idCount = 1000;
        CountDownLatch latch = new CountDownLatch(repeatCount);

        // when
        long start = System.nanoTime();
        for (int i = 0; i < repeatCount; i++) {
            executorService.submit(() -> {
                generateIdList(snowflakeService, idCount);
                latch.countDown();
            });
        }

        latch.await();

        long end = System.nanoTime();
        System.out.printf("times = %s ms%n", (end - start) / 1_000_000);

        executorService.shutdown();
    }
}
