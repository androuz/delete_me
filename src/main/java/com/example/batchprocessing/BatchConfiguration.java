package com.example.batchprocessing;
import com.example.batchprocessing.model.Child;
import com.example.batchprocessing.model.Parent;
import com.example.batchprocessing.repository.ChildRepository;
import com.example.batchprocessing.repository.ParentRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableBatchProcessing
@EnableAsync
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    // Define a Bean for the JsonItemReader
    @Bean
    public JsonItemReader<Parent> jsonItemReader() {
        System.out.println("Reader");
        return new JsonItemReaderBuilder<Parent>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Parent.class))
                .resource(new ClassPathResource("data.json"))
                .name("parentJsonItemReader")
                .build();
    }

    @Bean
    public ItemProcessor<Parent, Parent> processor() {
        System.out.println("processor");
        // Implement your processing logic here
        return item -> item; // Placeholder: simply returns the item unmodified
    }

    @Bean
    public ItemWriter<Parent> writer() {
        return new ItemWriter<Parent>() {

            @Autowired
            private ParentRepository parentRepository;

            @Autowired
            private ChildRepository childRepository;

            @Override
            @Transactional
            public void write(List<? extends Parent> parents) throws Exception {
                // Temporarily collect parents to save in a type-safe way
                List<Parent> parentsToSave = new ArrayList<>(parents);

                // Step 1: Save all parents to ensure they have generated IDs
                List<Parent> savedParents = parentRepository.saveAll(parentsToSave);

                // Step 2: For each parent, assign it to its children and collect all children
                List<Child> allChildren = new ArrayList<>();
                for (Parent parent : savedParents) {
                    List<Child> children = parent.getChildren();
                    if (children != null) {
                        for (Child child : children) {
                            child.setParent(parent);
                            allChildren.add(child);
                        }
                    }
                }

                // Step 3: Save all children in a batch
                childRepository.saveAll(allChildren);
            }
        };
    }

    @Bean
    public Step step1() {
        System.out.println("step1");
        return stepBuilderFactory.get("step1")
                .<Parent, Parent>chunk(10)
                .reader(jsonItemReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job importJsonJob() {
        System.out.println("importJsonJob");
        return jobBuilderFactory.get("importJsonJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("JobLaunch-");
        executor.initialize();
        return executor;
    }
}