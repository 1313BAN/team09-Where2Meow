package com.ssafy.where2meow.review.service;

import com.ssafy.where2meow.review.dto.UserReviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewService {
  public List<UserReviewResponse> getAllUserReview()
}
