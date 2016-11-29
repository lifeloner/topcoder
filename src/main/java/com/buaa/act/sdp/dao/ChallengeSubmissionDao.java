package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.challenge.ChallengeSubmission;

import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengeSubmissionDao {
    void insert(ChallengeSubmission [] challengeSubmission);
    ChallengeSubmission[] getChallengeSubmission(ChallengeSubmission challengeSubmission);
    List<Map<String,String>> getUserSubmissons();
}
