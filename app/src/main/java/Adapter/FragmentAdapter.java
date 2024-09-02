package Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Fragments.CallsFragment;
import Fragments.ChatsFragment;
import Fragments.StatusFragment;

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0)
        {
            return new ChatsFragment();
        }
        else if(position==1)
        {
            return new StatusFragment();
        }
        else if(position==2)
        {
            return new CallsFragment();
        }
        else
        {
            return new  ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position)
    {
        if(position==0)
        {
            return "Chats";
        }
        else if(position==1)
        {
            return "Status";
        }
        else if(position==2)
        {
            return "Calls";
        }
        return null;
    }
}
