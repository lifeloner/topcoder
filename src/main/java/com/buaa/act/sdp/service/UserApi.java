package com.buaa.act.sdp.service;

import com.buaa.act.sdp.bean.user.*;
import com.buaa.act.sdp.dao.*;
import com.buaa.act.sdp.util.HttpUtils;
import com.buaa.act.sdp.util.JsonUtil;
import com.buaa.act.sdp.util.RequestUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2016/10/15.
 */
@Service
public class UserApi {

    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DevelopmentDao developmentDao;

    @Autowired
    private DevelopmentHistoryDao developmentHistoryDao;

    @Autowired
    private RatingHistoryDao ratingHistoryDao;

    public void getUserByName(String userName) {
        String json = null;
        for(int i=0;i<10&&json==null;i++) {
            json=RequestUtil.request("http://api.topcoder.com/v2/users/" + userName);
        }
        //System.out.println(json);
        if (json != null) {
            System.out.println(userName);
            User user = JsonUtil.fromJson(json, User.class);
            String[]skills=getUserSkills(userName);
            if(skills!=null){
                user.setSkills(skills);
            }
            userDao.insert(user);
        }
        else {
            System.out.println();
        }
    }

    public String[] getUserSkills(String userName){
        String json= null;
        for(int i = 0;i<10&&json==null;i++){
            json=HttpUtils.httpGet("http://api.topcoder.com/v3/members/"+userName+"/skills");
        }
        if(json!=null){
            List<JsonElement>list=JsonUtil.getJsonElement(json,new String[]{"result","content","skills"});
            if(list!=null&&list.size()>0){
                JsonElement jsonElement=list.get(0);
                if(jsonElement!=null&&jsonElement.isJsonObject()){
                    Map<String,Skill>map=JsonUtil.jsonToMap(jsonElement.getAsJsonObject(),Skill.class);
                    String[]str=new String[map.size()];
                    int index=0;
                    for(Map.Entry<String,Skill>entry:map.entrySet()){
                        str[index++]=entry.getValue().getTagName();
                    }
                    return str;
                }
            }
        }
        return null;
    }
    public void handUserDevelopmentInfo(String handle, String json) {
        JsonElement jsonElement = JsonUtil.getJsonElement(json, "Tracks");

        if (jsonElement != null) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Map<String, Development> map = JsonUtil.jsonToMap(jsonObject, Development.class);
            List<Development> lists = new ArrayList<>();
            for (Map.Entry<String, Development> entry : map.entrySet()) {
                Development development = entry.getValue();
                development.setDevelopType(entry.getKey());
                development.setHandle(handle);
                lists.add(development);
            }
            if (lists.size() > 0) {
                developmentDao.insert(lists);
            }
        }
        jsonElement = JsonUtil.getJsonElement(json, "CompetitionHistory");
        if (jsonElement != null) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Map<String, DevelopmentHistory> map = JsonUtil.jsonToMap(jsonObject, DevelopmentHistory.class);
            List<DevelopmentHistory> lists = new ArrayList<>();
            for (Map.Entry<String, DevelopmentHistory> entry : map.entrySet()) {
                DevelopmentHistory developmentHistory = entry.getValue();
                developmentHistory.setDevelopType(entry.getKey());
                developmentHistory.setHandle(handle);
                lists.add(developmentHistory);
            }
            if (lists.size() > 0) {
                developmentHistoryDao.insert(lists);
            }
        }
    }
    //a
    public void getUserStatistics(String userName) {
        String string =null;
        for(int i=0;i<10&&string==null;i++){
            string=RequestUtil.request("http://api.topcoder.com/v2/users/" + userName + "/statistics/develop");
        }
        if (string != null) {
            //System.out.println(string);
            handUserDevelopmentInfo(userName, string);
        }
    }
    //rating
    //"challengeType should be an element of design,development,specification,architecture,bug_hunt,test_suites,assembly,ui_prototypes,conceptualization,ria_build,ria_component,test_scenarios,copilot_posting,content_creation,reporting,marathon_match,first2finish,code,algorithm."
    public void getUserChallengeHistory(String userName, String challengeType) {
        String json = null;
        for(int i=0;i<10&&json==null;i++){
            json=RequestUtil.request("http://api.topcoder.com/v2/develop/statistics/" + userName + "/" + challengeType);
        }
        if (json != null) {
            handUserRatingHistory(userName, challengeType, json);
        }
    }

    public void handUserRatingHistory(String userName, String challengeType, String json) {
        JsonElement jsonElement = JsonUtil.getJsonElement(json, "history");
        if (jsonElement != null) {
            RatingHistory[] ratingHistories = JsonUtil.fromJson(jsonElement, RatingHistory[].class);
            if (ratingHistories != null && ratingHistories.length > 0) {
                for (int i = 0; i < ratingHistories.length; i++) {
                    ratingHistories[i].setHandle(userName);
                    ratingHistories[i].setDevelopType(challengeType);
                }
                ratingHistoryDao.insert(ratingHistories);
            }
        }
    }

    public void saveUser(String handle){
        getUserByName(handle);
        getUserStatistics(handle);
        getUserChallengeHistory(handle,"design");
        getUserChallengeHistory(handle,"development");
        getUserChallengeHistory(handle,"specification");
        getUserChallengeHistory(handle,"architecture");
        getUserChallengeHistory(handle,"bug_hunt");
        getUserChallengeHistory(handle,"test_suites");
        getUserChallengeHistory(handle,"ui_prototypes");
        getUserChallengeHistory(handle,"conceptualization");
        getUserChallengeHistory(handle,"ria_build");
        getUserChallengeHistory(handle,"ria_component");
        getUserChallengeHistory(handle,"test_scenarios");
        getUserChallengeHistory(handle,"copilot_posting");
        getUserChallengeHistory(handle,"content_creation");
        getUserChallengeHistory(handle,"first2finish");
        getUserChallengeHistory(handle,"code");
    }


    public void saveAllUsers(){

        String[] names=challengeRegistrantDao.getUsers();
        for(int i=0;i<names.length;i++){
            System.out.print(i+":");
            saveUser(names[i]);
        }
    }

}
