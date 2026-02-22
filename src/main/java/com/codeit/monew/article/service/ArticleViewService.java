package com.codeit.monew.article.service;

import com.codeit.monew.article.repository.ArticleViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final ArticleViewRepository articleViewRepository;

}
