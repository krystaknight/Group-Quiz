package edu.umsl.quizlet.GroupListing;

import android.content.Context;

import java.util.List;

import edu.umsl.quizlet.database.UserInfoPersistence;
import edu.umsl.quizlet.dataClasses.Group;

/**
 * Created by Austin Edwards on 4/27/2017.
 */

public class GroupPageModel {
    private List<Group> mGroups;
    private UserInfoPersistence mPersistence;


    public GroupPageModel(Context context) {
        mPersistence = UserInfoPersistence.sharedInstance(context);
    }


    // Returns the Group Name from the phones db
    public String getGroupName() {
        return mPersistence.getGroup().getName();}

    // Returns the Entire Group object including Group Users
    public Group getGroup(){
        return mPersistence.getGroup();
    }

}
