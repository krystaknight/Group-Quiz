package edu.umsl.quizlet.GroupListing;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import edu.umsl.quizlet.R;
import edu.umsl.quizlet.dataClasses.GroupUser;

/**
 * Created by klkni on 5/4/2017.
 */

public class GroupHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mGroupMember;
    private GroupPageModel mModel;
    private int mPosition;

    public GroupHolder(View itemView) {
        super(itemView);
        mGroupMember = (TextView) itemView.findViewById(R.id.group_member_text_view);
        itemView.setOnClickListener(this);
    }

    public void bindGroup(GroupUser g) {
        if (g.getSingleQuizStatus().equals("complete")) {
            mGroupMember.setBackgroundResource(R.color.colorAccent);
        } else {
            mGroupMember.setBackgroundColor(Color.GRAY);
        }
        if (g.getLeader()) {
            mGroupMember.setTextColor(Color.YELLOW);
            mGroupMember.setText(g.getFirst() + " " + g.getLast() + " - Leader");
        } else {
            mGroupMember.setText(g.getFirst() + " " + g.getLast());
        }

    }

    public void onClick(View v) {
        Context context = v.getContext();
        if (mModel == null) {
            mModel = new GroupPageModel(context);
        }
        Log.e("COURSE LIST", "Clicked: " + this + " at position: " + getAdapterPosition());
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }
}