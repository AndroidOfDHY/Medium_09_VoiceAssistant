package com.zlh.voiceassistant.tool;
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  

import com.zlh.voiceassistant.R;
import com.zlh.voiceassistant.action.CallAction;
import com.zlh.voiceassistant.action.RunAction;
import com.zlh.voiceassistant.action.SearchAction;
import com.zlh.voiceassistant.action.SendAction;
import com.zlh.voiceassistant.activity.MusicActivity;


import android.app.Activity;  
import android.app.Dialog;  
import android.content.Context;  
import android.content.Intent;  
import android.view.Gravity;  
import android.view.View;  
import android.view.Window;  
import android.view.WindowManager.LayoutParams;  
import android.widget.AdapterView;  
import android.widget.AdapterView.OnItemClickListener;  
import android.widget.GridView;  
import android.widget.SimpleAdapter;  
import android.widget.Toast;  


public class GridDialog extends Dialog {  
      
    private List<int[]> griditem = new ArrayList<int[]>();  
    {  
        griditem.add(new int[] { R.drawable.phone, R.string.phone });//图片资源，标题,可自己设定  
        griditem.add(new int[] { R.drawable.sms, R.string.sms });  
        griditem.add(new int[] { R.drawable.musicsearch, R.string.musicsearch });  
        griditem.add(new int[] { R.drawable.appstart, R.string.appstart });  
        griditem.add(new int[] { R.drawable.websearch, R.string.websearch });  
    };  

    private GridView gridview;  

    public GridDialog(Context context, boolean cancelable,  
            OnCancelListener cancelListener) {  
        super(context, cancelable, cancelListener);  
    }  

    public GridDialog(Context context, int theme) {  
        super(context, theme);  
    }  

    private void initGrid() {  
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();  

        for (int[] item : griditem) {  
            Map<String, Object> map = new HashMap<String, Object>();  
            map.put("image", item[0]);  
            map.put("title", getContext().getString(item[1]));  
            items.add(map);  
        }  

        SimpleAdapter adapter = new SimpleAdapter(getContext(),  
                items, // 列表内容  
                R.layout.grid_item, new String[] { "title", "image" },  
                new int[] { R.id.item_text, R.id.item_image });  

        gridview = (GridView) findViewById(R.id.mygridview);  
        // 为GridView设置数据  
        gridview.setAdapter(adapter);  

    }  

    public GridDialog(Context context) {  

        super(context);  
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 灭掉对话框标题，要放在setContentView前面否则会报错  

        setContentView(R.layout.grid_dialog);  

        setCanceledOnTouchOutside(true);// 点击对话框外部取消对话框显示  
        LayoutParams lp = getWindow().getAttributes();  
        getWindow().setAttributes(lp);  
          
        getWindow().addFlags(LayoutParams.FLAG_BLUR_BEHIND);// 添加模糊效果  
          
        // 设置透明度，对话框透明(包括对话框中的内容)alpha在0.0f到1.0f之间。1.0完全不透明，0.0f完全透明  
        // lp.alpha = 0.5f;  

        lp.dimAmount = 0.1f;// 设置对话框显示时的黑暗度，0.0f和1.0f之间，在我这里设置成0.0f会出现黑屏状态，求解。  

        initGrid();// 添加表格按钮内容  
    }  

    /** 
     * 绑定事件到指定的Activity上 
     *  
     * @param activity 
     */  
    public void bindEvent(final Activity activity) {  

        setOwnerActivity(activity);// )把对话框附着到一个Activity上  

        gridview.setOnItemClickListener(new OnItemClickListener() {  

            public void onItemClick(AdapterView<?> parent, View v,  
                    int position, long id) {  

                switch (position) {// position从0开始，GridView中按钮的位置  
                case 0:  
                	CallAction callAction=new CallAction();
                	callAction.callName(activity,null);
                    break;
                case 1:
                	SendAction sendAction=new SendAction();
                	sendAction.sendName(activity, null);break;
                case 2:  
                	Intent intent = new Intent();
    				intent.setClass(activity, MusicActivity.class);
                	activity.startActivity(intent);break;
                case 3:
                	RunAction runAction=new RunAction();
                	runAction.RunAppName(activity, null);break;
                case 4:
                	SearchAction searchAction=new SearchAction();
                	searchAction.searchByText(activity, null);break;
                }  
            }  
        });  
    }  
}  