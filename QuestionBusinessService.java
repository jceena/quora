package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//This is service class for QuestionController.

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private UserDao userDao;


    //This method takes the authorization to authorize the request.
    //The access token is sent to getAuthToken in userAuthDao to get userAuthEntity.
    //This entity is checked to authorise. If the parameter are right then questionentities list is created using questionDao.
    //All the related exception are handled.
    public List<QuestionEntity> getAllQuestions(final String authorizationToken)throws AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorizationToken);

        if(userAuthEntity == null){//Checking if user is not signed in.
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if(userAuthEntity.getLogoutAt() != null){//Checking if user is logged out.
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
        }

        //Returning the list of questionEntities.
        List<QuestionEntity> questionEntities = questionDao.getAllQuestions();
        return questionEntities;
    }

    //This method takes the authorization to authorize the request and uuid of the user to get the user detail,
    // using this detail it fetches the question of the user.
    //The access token is sent to getAuthToken in userAuthDao to get userAuthEntity.
    //This entity is checked to authorise.
    // If the parameter are right then user details are fetched using uuid and using that questions are fetched from the database using QuestionDao.
    // Then questionentities list is created using questionDao.
    //All the related exception are handled.
    public List<QuestionEntity> getAllQuestionsByUser(final String uuid,final String authorizationToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorizationToken);

        if (userAuthEntity == null){//Chekcing if user is not signed in
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if (userAuthEntity.getLogoutAt() != null){//checking if user is signed out
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
        }

        UsersEntity usersEntity = userDao.getUser(uuid);
        if (usersEntity == null){//Checking if user exists.
            throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
        }

        //Returning the list of questionEntities.
        List<QuestionEntity> questionEntities = questionDao.getAllQuestionsByUser(usersEntity);
        return questionEntities;
    }

    public QuestionEntity deleteQuestion(final String questionUuid, final String authorizationToken) throws UserNotFoundException,
            AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorizationToken);
        String role = userAuthEntity.getUser().getRole();
        QuestionEntity questionEntity = questionDao.getQuestionByQuestionUuid(questionUuid);

        if(userAuthEntity == null){//Checking if user is not signed in.
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if(userAuthEntity.getLogoutAt() != null){//Checking if user is logged out.
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete a question");
        }


        else if(role.equals("admin") || userAuthEntity.getUser().getUuid().equals(questionEntity.getUser().getUuid())) {//checking if the role of the user is admin.
            QuestionEntity  questionsEntity = questionDao.deleteQuestion(questionUuid);
            if (questionEntity == null) {//checking if theq uestion to be deleted exist in the user table.
                throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
            }
            return questionsEntity;
        }
        throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");


    }

}