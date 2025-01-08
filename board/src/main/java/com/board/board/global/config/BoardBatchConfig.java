package com.board.board.global.config;

import com.board.board.board.repository.entity.Board;
import com.board.board.global.batch.BoardItemProcessor;
import com.board.board.global.batch.BoardItemReader;
import com.board.board.global.batch.BoardItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BoardBatchConfig {

    private final JobRepository jobRepository; // Job 관리
    private final PlatformTransactionManager transactionManager; // 트랜잭션 관리
    private final BoardItemWriter boardItemWriter;
    private final BoardItemReader boardItemReader;
    private final BoardItemProcessor boardItemProcessor;

    @Bean
    public Job boardJob() {
        return new JobBuilder("boardJob", jobRepository)
                .start(boardStep()) // Step 설정
                .build();
    }

    @Bean
    public Step boardStep() {
        return new StepBuilder("boardStep", jobRepository)
                .<Board, Board>chunk(1000, transactionManager) // Chunk 기반 처리
                .reader(boardItemReader) // Reader 설정
                .processor(boardItemProcessor) // Processor 설정
                .writer(boardItemWriter) // Writer 설정
                .build();
    }
}