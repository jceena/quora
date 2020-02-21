package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class QuestionController {
    @Autowired
    QuestionBusinessService questionService;
    QuestionEntity questionEntity;

    @RequestMapping(path = "/question/delete/{questionId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionUuid") String questionUuid, @RequestHeader("authorization") String authToken) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {
        QuestionEntity questionEntity = questionService.deleteQuestion(questionUuid, authToken);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionEntity.getId().toString());
        questionDeleteResponse.status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);


    }
}