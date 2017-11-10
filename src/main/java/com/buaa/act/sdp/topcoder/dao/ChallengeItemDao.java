package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2016/10/19.
 */

public interface ChallengeItemDao {

    void insert(ChallengeItem challengeItem);

    ChallengeItem getChallengeItemById(@Param("challengeId") int challengeId);

    List<Integer> getChallengeIds();

    List<ChallengeItem> getAllChallenges();

    void updateChallenge(ChallengeItem item);

    List<Map<String, Object>> getProjectId();

}