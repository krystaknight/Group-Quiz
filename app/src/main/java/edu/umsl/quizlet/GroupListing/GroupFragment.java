package edu.umsl.quizlet.GroupListing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import edu.umsl.quizlet.R;
import edu.umsl.quizlet.dataClasses.Group;
import edu.umsl.quizlet.dataClasses.GroupUser;

/**
 * Created by klkni on 5/2/2017.
 */

public class GroupFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private GroupAdapter mGroupAdapter;
    private WeakReference<GroupListingViewDataSource> mDataSource;
    FragmentManager mManager;

    public void setDataSource(GroupListingViewDataSource dataSource) {
        mDataSource = new WeakReference<>(dataSource);
    }
    public interface GroupListingViewDataSource {
        Group getGroup();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_listing_frag, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.group_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new GroupFragment.GroupAdapter());
        return view;
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupHolder> {
        private ArrayList<GroupUser> mGroups;

        @Override
        public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.group_item_layout, parent, false);
            return new GroupHolder(view);
        }

        @Override
        public void onBindViewHolder(GroupHolder holder, int position) {
            if (mDataSource != null) {
                holder.bindGroup(mDataSource.get().getGroup().getGroupUsers().get(position));
                holder.setmPosition(position);
            }
        }

        @Override
        public int getItemCount() {
            if (mDataSource != null) {
                return mDataSource.get().getGroup().getGroupUsers().size();
            }
            Log.e("NO COURSES", "This User has no courses.");
            return 0;
        }

    }
    public void changeView(){
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

}
