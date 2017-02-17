package com.example.turtlejk.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turtlejk.myapplication.Imagepicker.GlideImageLoader;
import com.example.turtlejk.myapplication.Imagepicker.ImagePickerAdapter;
import com.example.turtlejk.myapplication.Imagepicker.ImagePreviewandDelActivity;
import com.example.turtlejk.myapplication.Imagepicker.WxDemoActivity;
import com.example.turtlejk.myapplication.Model.DataBean;
import com.example.turtlejk.myapplication.Model.OptionBean;
import com.example.turtlejk.myapplication.Model.ResultBean;
import com.example.turtlejk.myapplication.fragment.FragmentsActivity;
import com.example.turtlejk.myapplication.util.FileUploadActivity;
import com.example.turtlejk.myapplication.util.GetPathFromUri4kitkat;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.turtlejk.myapplication.R.drawable.apptheme_textfield_activated_holo_light;

public class MainActivity extends AppCompatActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    private List<ResultBean> resultbeanlist = new ArrayList<ResultBean>();
    private List<DataBean> databeanlist = new ArrayList<DataBean>();
    private LinearLayout mainlinearlayout;
    private LinearLayout fileupload;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private static final int REQUEST_CAMERA_CODE = 10;
    private static final int REQUEST_PREVIEW_CODE = 20;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private static final int FILE_SELECT_CODE = 0;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 5;               //允许选择图片最大数
    private List<File> addfilelist = new ArrayList<File>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //修改状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置顶部状态栏颜色
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        setContentView(R.layout.activity_main);
        readjson();
        creatView();
        LinearLayout steppicture = (LinearLayout) findViewById(R.id.steppicture);
        LinearLayout treepicture = (LinearLayout) findViewById(R.id.treepicture);
        steppicture.setOnClickListener(stepOnClick);
        treepicture.setOnClickListener(treeOnClick);
        LinearLayout fileupload = (LinearLayout) findViewById(R.id.fileupload);
        LinearLayout addfile = (LinearLayout) findViewById(R.id.addfile);
        addfile.setOnClickListener(addfileOnClick);
        initImagePicker();
        initWidget();
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制

    }

    private void initWidget() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.myrecyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);
        int space = 30;
        int dpspace = px2dip(this, space);
        recyclerView.addItemDecoration(new SpacesItemDecoration(dpspace));
        recyclerView.setAdapter(adapter);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewandDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                GetPathFromUri4kitkat getPathFromUri = new GetPathFromUri4kitkat();
                String path = getPathFromUri.getPath(MainActivity.this, uri);
                String type = getExtensionName(path);
                String name = getFileName(path);
                System.out.println("------------" + name);
                File file = new File(path);
                System.out.println("------------" + file.toString());
                long tempsize = 0;
                try {
                    tempsize = getFileSize(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String size = FormetFileSize(tempsize);
                System.out.println("------------" + size);
                int flag = creatAddfile(path, type, name, size);
                if (flag == 1) {
                    addfilelist.add(file);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                selImageList.addAll(images);
                adapter.setImages(selImageList);
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                selImageList.clear();
                selImageList.addAll(images);
                adapter.setImages(selImageList);
            }
        }
    }

    View.OnClickListener stepOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, FragmentsActivity.class);
            //用Bundle携带数据
            Bundle bundle = new Bundle();
            //传递name参数为tinyphp
            bundle.putInt("name", 0);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    View.OnClickListener treeOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, FragmentsActivity.class);
            //用Bundle携带数据
            Bundle bundle = new Bundle();
            //传递name参数为tinyphp
            bundle.putInt("name", 1);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    View.OnClickListener addfileOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            showFileChooser();
        }
    };

    public void readjson() {
        try {
            //从assets获取json文件
            InputStreamReader isr = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("assets/" + "data.json"));
            //字节流转字符流
            BufferedReader bfr = new BufferedReader(isr);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bfr.readLine()) != null) {
                stringBuilder.append(line);
            }//将JSON数据转化为字符串
            JSONObject root = new JSONObject(stringBuilder.toString());
            //根据键名获取键值信息
            String fields = root.getString("fields");
            System.out.println("root:" + root.getString("fields"));
            //顶部信息
            //TextView servicename = (TextView) findViewById(R.id.servicename);
            //TextView serviceid = (TextView) findViewById(R.id.serviceid);
            //TextView pri = (TextView) findViewById(R.id.pri);
            JSONArray array = root.getJSONArray("data");
            System.out.println("------------------");
            for (int i = 0; i < array.length(); i++) {
                DataBean databean = new DataBean();
                JSONObject dataobject = array.getJSONObject(i);
                if ("macros".equals(dataobject.getString("leipiplugins"))) {
                    databean.setLeipiplugins("macros");
                    databean.setType("text");
                    databean.setValue("{macros." + dataobject.getString("orgtype") + "}");
                    databean.setOrgtype(dataobject.getString("orgtype"));
                    databean.setStyle(dataobject.getString("style"));
                    databean.setOrgwidth(dataobject.getInt("orgwidth"));
                    databean.setOrghide(dataobject.getString("orghide"));
                } else if ("text".equals(dataobject.getString("leipiplugins"))) {
                    databean.setLeipiplugins("text");
                    databean.setType("text");
                    databean.setValue(dataobject.getString("value"));
                    databean.setOrgtype(dataobject.getString("orgtype"));
                    databean.setStyle(dataobject.getString("style"));
                    databean.setOrgwidth(dataobject.getInt("orgwidth"));
                    databean.setOrghide(dataobject.getString("orghide"));
                } else if ("textarea".equals(dataobject.getString("leipiplugins"))) {
                    databean.setLeipiplugins("textarea");
                    databean.setValue(dataobject.getString("value"));
                    databean.setStyle(dataobject.getString("style"));
                    databean.setOrgwidth(dataobject.getInt("orgwidth"));
                    databean.setOrgheight(dataobject.getInt("orgheight"));
                } else if ("select".equals(dataobject.getString("leipiplugins"))) {
                    databean.setLeipiplugins("select");
                    databean.setSelected("selected");
                    databean.setStyle(dataobject.getString("style"));
                    databean.setOrgwidth(dataobject.getInt("orgwidth"));
                    databean.setSize(dataobject.getInt("size"));
                    databean.setValue(dataobject.getString("value"));
                } else if ("radios".equals(dataobject.getString("leipiplugins"))) {
                    databean.setLeipiplugins("radios");
                    List<OptionBean> optionbeanlist = new ArrayList<OptionBean>();
                    JSONArray options = dataobject.getJSONArray("options");
                    for (int m = 0; m < options.length(); m++) {
                        JSONObject optionobject = options.getJSONObject(m);
                        OptionBean optionbean = new OptionBean();
                        optionbean.setName(optionobject.getString("name"));
                        optionbean.setValue(optionobject.getString("value"));
                        optionbean.setType(optionobject.getString("type"));
                        optionbeanlist.add(optionbean);
                    }
                    databean.setOptionBeanList(optionbeanlist);
                    databean.setValue(dataobject.getString("value"));
                } else if ("checkboxs".equals(dataobject.getString("leipiplugins"))) {
                    databean.setLeipiplugins("checkboxs");
                    List<OptionBean> optionbeanlist = new ArrayList<OptionBean>();
                    JSONArray options = dataobject.getJSONArray("options");
                    for (int m = 0; m < options.length(); m++) {
                        JSONObject optionobject = options.getJSONObject(m);
                        OptionBean optionbean = new OptionBean();
                        optionbean.setName(optionobject.getString("name"));
                        optionbean.setValue(optionobject.getString("value"));
                        optionbean.setType(optionobject.getString("type"));
                        optionbeanlist.add(optionbean);
                    }
                    databean.setOptionBeanList(optionbeanlist);
                    databean.setValue(dataobject.getString("value"));
                }
                databean.setTitle(dataobject.getString("title"));
                databean.setName(dataobject.getString("name"));
                databean.setContent(dataobject.getString("content"));
                databeanlist.add(databean);
                System.out.println(databean.getContent());
            }
            bfr.close();
            isr.close();//依次关闭流
            System.out.println("------------------");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void creatView() {
        mainlinearlayout = (LinearLayout) findViewById(R.id.mainlinearlayout);
        for (int i = 0; i < databeanlist.size(); i++) {
            creatwidget(databeanlist.get(i));
        }
    }


    public void creatwidget(final DataBean databean) {
        String leipiplugins = databean.getLeipiplugins();
        int firstmarginleftwidth = 30;
        int firstmargintopwidth = 20;
        int firstmarginbottomwidth = 20;
        int firstmarginrightwidth = 44;
        int tvheight = 48;
        int tvsize = 34;
        int ivwidth = 16;
        int ivheight = 26;
        int ivmargintop = 32;
        int ivmarginbottom = 30;
        int ivmarginright = 30;
        int linearheight = 88;
        if ("text".equals(leipiplugins) || "macros".equals(leipiplugins)) {
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.HORIZONTAL);
            root.setBackgroundColor(Color.WHITE);
            root.setId(View.generateViewId());
            LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, linearheight);
            root.setLayoutParams(rootParams);

            TextView tv1 = new TextView(this);
            tv1.setId(View.generateViewId());
            String title = databean.getTitle();
            String regex = "[\u4e00-\u9fff]";
            int count = (" " + title + " ").split(regex).length - 1;
            int tvwidth = count * 34;
            int tempwidth = 136 - tvwidth;
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvsize);
            tv1.setText(databean.getTitle());
            LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(tvwidth, tvheight);
            tv1Params.setMargins(firstmarginleftwidth, 0, firstmarginrightwidth + tempwidth, firstmarginbottomwidth);
            root.addView(tv1, tv1Params);

            EditText et1 = new EditText(this);
            et1.setId(View.generateViewId());
            et1.setText(databean.getValue());
            et1.setSingleLine(true);
            et1.setBackgroundResource(R.drawable.apptheme_textfield_default_holo_light);
            et1.setBackground(null);
            et1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
            LinearLayout.LayoutParams et1Params = new LinearLayout.LayoutParams(540, linearheight);
            et1Params.setMargins(firstmarginleftwidth, 0, 0, 20);
            root.addView(et1, et1Params);

            mainlinearlayout.addView(root, rootParams);
        } else if ("textarea".equals(leipiplugins)) {
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.HORIZONTAL);
            root.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
            root.setLayoutParams(rootParams);

            TextView tv1 = new TextView(this);
            tv1.setId(View.generateViewId());
            String title = databean.getTitle();
            String regex = "[\u4e00-\u9fff]";
            int count = (" " + title + " ").split(regex).length - 1;
            int tvwidth = count * 34;
            int tempwidth = 136 - tvwidth;
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvsize);
            tv1.setText(databean.getTitle());
            LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(tvwidth, tvheight);
            tv1Params.setMargins(firstmarginleftwidth, 0, firstmarginrightwidth + tempwidth, 82);
            root.addView(tv1, tv1Params);

            EditText et1 = new EditText(this);
            et1.setId(View.generateViewId());
            String etvalue = databean.getValue();
            int etcount = (" " + etvalue + " ").split(regex).length - 1;
            int etwidth = count * 34;
            et1.setText(databean.getValue());
            et1.setBackground(null);
            et1.setBackgroundResource(R.drawable.apptheme_textfield_default_holo_light);
            et1.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvsize);
            et1.setBackgroundResource(R.drawable.apptheme_edit_text_holo_light);
            LinearLayout.LayoutParams et1Params = new LinearLayout.LayoutParams(540, linearheight);
            et1Params.setMargins(firstmarginleftwidth, 0, 0, 77);
            root.addView(et1, et1Params);

            mainlinearlayout.addView(root, rootParams);
        } else if ("select".equals(leipiplugins)) {
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.HORIZONTAL);
            root.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, linearheight);
            root.setLayoutParams(rootParams);

            final TextView tv1 = new TextView(this);
            tv1.setId(View.generateViewId());
            final String title = databean.getTitle();
            String regex = "[\u4e00-\u9fff]";
            int count = (" " + title + " ").split(regex).length - 1;
            int tvwidth = count * 34;
            int tempwidth = 136 - tvwidth;
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvsize);
            tv1.setText(databean.getTitle());
            LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(tvwidth, tvheight);
            tv1Params.setMargins(firstmarginleftwidth, firstmargintopwidth, firstmarginrightwidth + tempwidth, firstmarginbottomwidth);
            tv1Params.weight = 0;
            root.addView(tv1, tv1Params);

            final TextView tv2 = new TextView(this);
            tv2.setId(View.generateViewId());

            if ("selected".equals(databean.getSelected())) {
                String content = databean.getContent();
                Pattern p = Pattern.compile("option selected=\"selected\" value=\"(.*?)\"");
                Matcher m = p.matcher(content);
                while (m.find()) {
                    String selectedvalue = m.group(1);
                    tv2.setText(selectedvalue);
                }
            }

            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(title);
                    //    指定下拉列表的显示数据
                    //    设置一个下拉的列表选择项
                    final String[] strArray = databean.getValue().split(",");
                    builder.setItems(strArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv2.setText(strArray[which]);
                        }
                    });
                    builder.show();
                }
            });
            String value = tv2.getText().toString();
            String tv2regex = "[\u4e00-\u9fff]";
            int tv2count = (" " + value + " ").split(regex).length - 1;
            int tv2width = count * 34;
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvsize);
            LinearLayout.LayoutParams tv2Params = new LinearLayout.LayoutParams(tv2width, tvheight);
            tv2Params.weight = 1;
            tv2Params.setMargins(0, firstmargintopwidth, 0, firstmarginbottomwidth);
            root.addView(tv2, tv2Params);

            ImageView iv1 = new ImageView(this);
            iv1.setImageResource(R.drawable.hevron);
            iv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(title);
                    //    指定下拉列表的显示数据
                    //    设置一个下拉的列表选择项
                    final String[] strArray = databean.getValue().split(",");
                    builder.setItems(strArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv2.setText(strArray[which]);
                        }
                    });
                    builder.show();
                }
            });
            LinearLayout.LayoutParams iv1Params = new LinearLayout.LayoutParams(ivwidth, ivheight);
            iv1Params.weight = 0;
            iv1Params.setMargins(0, ivmargintop, ivmarginright, ivmarginbottom);
            root.addView(iv1, iv1Params);
            mainlinearlayout.addView(root, rootParams);
        } else if ("radios".equals(leipiplugins)) {
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.HORIZONTAL);
            root.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, linearheight);
            root.setLayoutParams(rootParams);

            final TextView tv1 = new TextView(this);
            tv1.setId(View.generateViewId());
            final String title = databean.getTitle();
            String regex = "[\u4e00-\u9fff]";
            int count = (" " + title + " ").split(regex).length - 1;
            int tvwidth = count * 34;
            int tempwidth = 136 - tvwidth;
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvsize);
            tv1.setText(databean.getTitle());
            LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(tvwidth, tvheight);
            tv1Params.setMargins(firstmarginleftwidth, firstmargintopwidth, firstmarginrightwidth + tempwidth, firstmarginbottomwidth);
            tv1Params.weight = 0;
            root.addView(tv1, tv1Params);

            final TextView tv2 = new TextView(this);
            tv2.setId(View.generateViewId());

            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(title);
                    //    设置一个下拉的列表选择项
                    final String[] strArray = databean.getValue().split(",");
                    builder.setSingleChoiceItems(strArray, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv2.setText(strArray[which]);
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
            String value = tv2.getText().toString();
            String tv2regex = "[\u4e00-\u9fff]";
            int tv2count = (" " + value + " ").split(regex).length - 1;
            int tv2width = count * 34;
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvsize);
            LinearLayout.LayoutParams tv2Params = new LinearLayout.LayoutParams(tv2width, tvheight);
            tv2Params.weight = 1;
            tv2Params.setMargins(0, firstmargintopwidth, 0, firstmarginbottomwidth);
            root.addView(tv2, tv2Params);

            ImageView iv1 = new ImageView(this);
            iv1.setImageResource(R.drawable.hevron);
            iv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(title);
                    //    设置一个下拉的列表选择项
                    final String[] strArray = databean.getValue().split(",");
                    builder.setSingleChoiceItems(strArray, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv2.setText(strArray[which]);
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
            LinearLayout.LayoutParams iv1Params = new LinearLayout.LayoutParams(ivwidth, ivheight);
            iv1Params.weight = 0;
            iv1Params.setMargins(0, ivmargintop, ivmarginright, ivmarginbottom);
            root.addView(iv1, iv1Params);
            mainlinearlayout.addView(root, rootParams);
        } else if ("checkboxs".equals(leipiplugins)) {
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            root.setLayoutParams(rootParams);

            LinearLayout tvroot = new LinearLayout(this);
            tvroot.setOrientation(LinearLayout.VERTICAL);
            tvroot.setBackgroundColor(Color.parseColor("#F9F9F9"));
            LinearLayout.LayoutParams tvrootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, linearheight);
            tvroot.setLayoutParams(tvrootParams);

            final TextView tv1 = new TextView(this);
            tv1.setId(View.generateViewId());
            String title = databean.getTitle();
            String regex = "[\u4e00-\u9fff]";
            int count = (" " + title + " ").split(regex).length - 1;
            int tvwidth = count * 28;
            int tempgwidth = 720 - tvwidth;
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28);
            tv1.setText(databean.getTitle());
            LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(tvwidth, 40);
            tv1Params.setMargins(30, 30, tempgwidth, 16);
            tvroot.addView(tv1, tv1Params);
            root.addView(tvroot, tvrootParams);

            final String[] strArray = databean.getValue().split(",");
            LinearLayout checkbox = new LinearLayout(this);
            checkbox.setOrientation(LinearLayout.VERTICAL);
            checkbox.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            checkbox.setLayoutParams(checkboxParams);
            for (int i = 0; i < databean.getOptionBeanList().size(); i++) {
                LinearLayout checkboxitem = new LinearLayout(this);
                checkboxitem.setOrientation(LinearLayout.HORIZONTAL);
                checkboxitem.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams checkboxitemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                checkboxitem.setLayoutParams(checkboxitemParams);

                final ImageView iv1 = new ImageView(MainActivity.this);
                iv1.setId(View.generateViewId());
                if (0 == i) {
                    iv1.setImageResource(R.drawable.checked);
                } else {
                    iv1.setImageResource(R.drawable.fill);
                }
                iv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ImageView iv0 = (ImageView) findViewById(arg0.getId());
                        ImageView tempiv = new ImageView(MainActivity.this);
                        tempiv.setImageResource(R.drawable.checked);
                        if (iv0.getDrawable().getCurrent().getConstantState().equals(getResources().getDrawable(R.drawable.checked).getConstantState())) {
                            iv0.setImageResource(R.drawable.fill);
                        } else {
                            iv0.setImageResource(R.drawable.checked);
                        }
                    }
                });
                LinearLayout.LayoutParams iv1Params = new LinearLayout.LayoutParams(46, 46);
                iv1Params.setMargins(30, 20, 20, 20);
                checkboxitem.addView(iv1, iv1Params);


                TextView tv2 = new TextView(this);
                tv2.setId(View.generateViewId());
                tv2.setText(strArray[i]);
                String tv2title = tv2.getText().toString();
                int tv2count = (" " + title + " ").split(regex).length - 1;
                int tv2width = count * 34;
                int tempwidth = 654 - tvwidth;
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvsize);
                LinearLayout.LayoutParams tv2Params = new LinearLayout.LayoutParams(tvwidth, tvheight);
                tv2Params.setMargins(0, 20, tempwidth, 20);
                checkboxitem.addView(tv2, tv2Params);

                checkbox.addView(checkboxitem, checkboxitemParams);
            }
            root.addView(checkbox, checkboxParams);
            mainlinearlayout.addView(root, rootParams);
        }

    }

    public int creatAddfile(String path, String type, String name, String size) {
        fileupload = (LinearLayout) findViewById(R.id.fileupload);
        int flag = 0;
        final LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(Color.parseColor("#CCCCCC"));
        final LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(690, 116);
        rootParams.setMargins(30, 30, 30, 0);
        root.setLayoutParams(rootParams);

        TextView tv1 = new TextView(MainActivity.this);
        tv1.setId(View.generateViewId());
        String Uptype = type.toUpperCase();
        System.out.println("----------" + Uptype);
        tv1.setText(Uptype);
        int count = 0;
        for (int i = 0; i < Uptype.length(); i++) {
            char cs = Uptype.charAt(i);
            if (((cs >= 'A' && cs <= 'Z'))) {
                count++;
            }
        }
        Paint pFont = new Paint();
        Rect rect = new Rect();
        pFont.getTextBounds(Uptype, 0, 1, rect);
        int uptypewidth = rect.width();
        TextPaint textPaint = tv1.getPaint();
        float textPaintWidth = textPaint.measureText(Uptype);
        System.out.println(textPaintWidth + "ddddddddddddddd");
        int tv1width = (int) textPaintWidth;
        tv1.setTextColor(Color.parseColor("#FFFFFF"));
        tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48);
        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(tv1width - 12, 67);
        tv1Params.setMargins(14, 4, 14, 4);


        LinearLayout tvroot = new LinearLayout(this);
        tvroot.setOrientation(LinearLayout.VERTICAL);
        tvroot.setBackgroundColor(Color.parseColor("#009760"));
        LinearLayout.LayoutParams tvrootParams = new LinearLayout.LayoutParams(tv1width + 17, 76);
        tvrootParams.setMargins(32, 20, 20, 20);
        tvrootParams.weight = 1;
        tvroot.setLayoutParams(tvrootParams);
        tvroot.addView(tv1, tv1Params);
        root.addView(tvroot, tvrootParams);


        LinearLayout tv2root = new LinearLayout(this);
        tv2root.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams tv2rootParams = new LinearLayout.LayoutParams(400, 88);
        tv2rootParams.setMargins(0, 15, 15, 34);
        tv2rootParams.weight = 1;
        tv2root.setLayoutParams(tv2rootParams);

        TextView tv2 = new TextView(this);
        tv2.setId(View.generateViewId());
        Paint pFont1 = new Paint();
        Rect rect1 = new Rect();
        pFont1.getTextBounds(name, 0, 1, rect);
        int namewidth = rect1.width();
        LinearLayout.LayoutParams tv2Params;
        if (namewidth > 340) {
            tv2Params = new LinearLayout.LayoutParams(400, 48);
        } else {
            tv2Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 48);
        }
        tv2.setSingleLine(true);
        tv2.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        tv2.setTextColor(Color.parseColor("#000000"));
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 34);
        tv2.setText(name);
        tv2Params.setMargins(0, 0, 0, 3);
        tv2root.addView(tv2, tv2Params);

        TextView tv3 = new TextView(this);
        tv3.setId(View.generateViewId());
        int tv3width = count * 31;
        tv3.setSingleLine(true);
        tv3.setTextColor(Color.parseColor("#888888"));
        tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
        tv3.setText(Uptype + " " + size);
        LinearLayout.LayoutParams tv3Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 37);
        tv3Params.setMargins(0, 0, 0, 0);
        tv2root.addView(tv3, tv3Params);
        root.addView(tv2root, tv2rootParams);

        final ImageView iv = new ImageView(this);
        iv.setId(View.generateViewId());
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                System.out.println("00000000000000");
                fileupload.removeView(root);
            }
        });
        iv.setImageResource(R.drawable.z);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(50, 50);
        ivParams.setMargins(0, 33, 30, 33);
        ivParams.weight = 0;
        root.addView(iv, ivParams);

        fileupload = (LinearLayout) findViewById(R.id.fileupload);
        fileupload.addView(root, rootParams);
        flag = 1;
        return flag;
    }

    /**
     * 选择文件
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//过滤文件类型（所有）
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "请选择文件！"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "未安装文件管理器！", Toast.LENGTH_SHORT).show();
        }
    }

    public String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public String getFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }
    }

    public long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            Toast.makeText(this, "文件不存在！", Toast.LENGTH_SHORT).show();
        }
        return size;
    }

    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

}
