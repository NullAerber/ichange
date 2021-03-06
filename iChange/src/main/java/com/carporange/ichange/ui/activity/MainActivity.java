package com.carporange.ichange.ui.activity;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.carporange.ichange.R;
import com.carporange.ichange.fragment.MainFragment;
import com.carporange.ichange.ui.base.CarporangeBaseActivity;
import com.carporange.ichange.util.DensityUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.event.RefreshEvent;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

public class MainActivity extends CarporangeBaseActivity implements MainFragment.OnHomeClickListener {
    ConnectivityManager manager;

    private DrawerLayout mDrawerLayout;
    private View mFragmentLeft;
    private Fragment mMainFragment;
    private LinearLayout ll_nav_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        checkNetworkState();

        //监听退出应用按钮
        ll_nav_exit = (LinearLayout) findViewById(R.id.ll_nav_exit);
        ll_nav_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initViews() {
        setContentView(R.layout.activity_main);

        //connent server
        User user = BmobUser.getCurrentUser(this, User.class);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Logger.i(getString(R.string.chat_connect_success));
                    //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                    EventBus.getDefault().post(new RefreshEvent());
                } else {
                    Logger.e(e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });

        //init view
        mFragmentLeft = findViewById(R.id.fragment_left);
        ViewGroup.LayoutParams layoutParams = mFragmentLeft.getLayoutParams();
        layoutParams.width = DensityUtil.getDisplayWidth(this) * 4 / 5;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentManager fm = getSupportFragmentManager();
        if (mMainFragment == null) {
            mMainFragment = new MainFragment();
        }
        fm.beginTransaction().replace(R.id.framelayout, mMainFragment).commit();
    }

    @Override
    public void initActionBar() {
    }


    @Override
    public void onHomeClick() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }


    // 不退出程序，进入后台
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    /**
//     * 检测网络是否连接
//     * @return
//     */
//    private boolean checkNetworkState() {
//        boolean flag = false;
//        //得到网络连接信息
//        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        //去进行判断网络是否连接
//        if (manager.getActiveNetworkInfo() != null) {
//            flag = manager.getActiveNetworkInfo().isAvailable();
//        }
//        if (!flag) {
//            setNetwork();
//        }
//
//        return flag;
//    }
//
//
//    /**
//     * 网络未连接时，调用设置方法
//     */
//    private void setNetwork(){
//        Toast.makeText(this, getString(R.string.NO_NETWORK), Toast.LENGTH_SHORT).show();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setIcon(R.drawable.icon_launch);
//        builder.setTitle("网络提示信息");
//        builder.setMessage(getString(R.string.NO_NETWORK));
//        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = null;
//                /**
//                 * 判断手机系统的版本！如果API大于10 就是3.0+
//                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
//                 */
//                if (android.os.Build.VERSION.SDK_INT > 10) {
//                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
//                } else {
//                    intent = new Intent();
//                    ComponentName component = new ComponentName(
//                            "com.android.settings",
//                            "com.android.settings.WirelessSettings");
//                    intent.setComponent(component);
//                    intent.setAction("android.intent.action.VIEW");
//                }
//                startActivity(intent);
//            }
//        });
//
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.create();
//        builder.show();
//    }

}
