package com.lyj.securitydomo.config;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.dto.PostDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.LOOSE);

        // Post to PostDTO 매핑 규칙 추가
        modelMapper.addMappings(new PropertyMap<Post, PostDTO>() {
            @Override
            protected void configure() {
                map(source.getUser().getUsername(), destination.getUsername());
            }
        });

        return modelMapper;
    }
}