package com.mwf.analyze.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.mwf.analyze.R;
import com.mwf.analyze.bean.AnalyzeBean;
import com.mwf.analyze.dao.AnalyzeDao;
import com.mwf.analyze.services.ParseTermService;
import com.mwf.analyze.services.ParseWordsService;
import com.mwf.analyze.utils.FileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 1、打开文件
 * 2、开启子线程读取文件数据并分组
 * 3、开启IntentService对每组请求数据
 * 4、对返回数据进行处理
 */
public class AnalyzePoemActivity extends AppCompatActivity implements View.OnClickListener, ParseTermService.IUpdateUI, ParseTermService.ILoadFinish, ParseWordsService.ILoadWorsdFinish, ParseWordsService.ILoadWorsdUpdateUI {

    private final String TAG = this.getClass().getName();

    @BindView(R.id.button_terms)
    Button button_terms;
    @BindView(R.id.button_word)
    Button button_word;
    @BindView(R.id.button_export)
    Button button_export;
    @BindView(R.id.button_inport)
    Button button_inport;
    @BindView(R.id.button_clear)
    Button button_clear;
    @BindView(R.id.txt_content)
    TextView mTxtContent;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_poem);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        showData();

    }

    /**
     * 展示数据
     */
    private void showData() {
        mTxtContent.setText("");
        //显示上一次的数据
        AnalyzeDao dao = new AnalyzeDao(AnalyzePoemActivity.this);
        List<AnalyzeBean> list = dao.queryAll(300, false);
        List<AnalyzeBean> list2 = dao.queryAll();
        Log.e(TAG, "总数：" + list2.size());
        String text;
        for (int i = 0; i < list.size(); i++) {
            text = mTxtContent.getText().toString();
            text += (list.get(i).getName() + "       ==" + list.get(i).getAmount() + "\n");
            mTxtContent.setText(text);
        }
    }

    private void showProgress(String content) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(content);
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog == null) {
            return;
        }
        progressDialog.dismiss();
    }


    /**
     * 打开文件选择器
     */
    private void openFile(DialogSelectionListener listener) {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        FilePickerDialog dialog = new FilePickerDialog(AnalyzePoemActivity.this, properties);
        dialog.setTitle("选择一个文件");
        dialog.setDialogSelectionListener(listener);
        dialog.show();
    }

    /**
     * 词统计
     */
    private void parseTerms() {
        DialogSelectionListener listener = new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length >= 0) {
                    readTermThread(files[0]);
                    //清空内容
                    mTxtContent.setText("");

                }
            }
        };
        openFile(listener);
    }

    /**
     * 开启线程读取文件内容
     */
    private void readTermThread(final String path) {

        //每组文字的长度
        final int arrayLength = 50;

        Thread mThread = new Thread() {
            @Override
            public void run() {
                String string = FileUtils.readTxtFile(path);
                if (!TextUtils.isEmpty(string)) {
                    double total = string.length();
                    double d = new Double(Math.round(total / arrayLength));
                    int size = (int) (Math.ceil(d));
                    Log.e(TAG, "总长度=" + total);
                    Log.e(TAG, "d=" + d);
                    Log.e(TAG, "分组=" + size);

                    //初始化IntentService
                    Intent intent = new Intent(AnalyzePoemActivity.this, ParseTermService.class);
                    ParseTermService.setUpdateUI(AnalyzePoemActivity.this);
                    ParseTermService.setLoadFinish(AnalyzePoemActivity.this);
                    //分组查询文字
                    for (int i = 0; i < size; i++) {
                        String singleString = "";
                        if (i == size - 1) {
                            //最后一组长度为总长度
                            singleString = string.substring(i * arrayLength, string.length());
                        } else {
                            //截取每一组的文字
                            singleString = string.substring(i * arrayLength, (i + 1) * arrayLength);
                        }
                        Log.e(TAG, "第" + i + "组");
                        Log.e(TAG, singleString);
                        //将每组数据传送给IntentService做处理
                        intent.putExtra("poemString", singleString);
                        intent.putExtra("number", i);
                        intent.putExtra("total", size);
                        //开启一个IntentService
                        startService(intent);
                    }
                }
            }
        };
        mThread.start();
    }


    @Override
    public void updateUI(Message message) {
        UIHandler.sendMessage(message);
    }

    /**
     * 更新UI操作
     */
    private final Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String something = bundle.getString("something");
            double total = bundle.getInt("total", 0);
            double number = bundle.getInt("number", 0);
            double lastPercent = number / total * 100;
            DecimalFormat df = new DecimalFormat("#.##");
            mTxtContent.setText("完成比例：  " + df.format(lastPercent) + "%，请耐心等候！");
        }
    };

    /**
     * 分词加载完毕
     */
    @Override
    public void loadFinsh() {
        loadFinishHandler.sendEmptyMessage(0);
    }

    /**
     * 分词加载完毕
     */
    private final Handler loadFinishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mTxtContent.setText("");
            AnalyzeDao dao = new AnalyzeDao(AnalyzePoemActivity.this);
            List<AnalyzeBean> list = dao.queryAll(200, false);
            String text;
            for (int i = 0; i < list.size(); i++) {
                text = mTxtContent.getText().toString();
                text += (list.get(i).getName() + "       ==" + list.get(i).getAmount() + "\n");
                mTxtContent.setText(text);
            }
        }
    };

    /**
     * 单字统计
     */
    private void parseWords() {
        DialogSelectionListener listener = new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length >= 0) {
                    //清空内容
                    mTxtContent.setText("");
                    //初始化IntentService
                    Intent intent = new Intent(AnalyzePoemActivity.this, ParseWordsService.class);
                    intent.putExtra("filePath", files[0]);
                    startService(intent);
                    ParseWordsService.setLoadFinish(AnalyzePoemActivity.this);
                    ParseWordsService.setLoadWorsdUpdateUI(AnalyzePoemActivity.this);
                }
            }
        };
        openFile(listener);
    }

    /**
     * 单字统计结束
     */
    @Override
    public void LoadWorsdFinish() {
        loadFinishHandler.sendEmptyMessage(0);
    }

    /**
     * 导出完成更新UI
     */
    private final Handler exportFinishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissProgress();
            Toast.makeText(AnalyzePoemActivity.this, "导出成功", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 导出数据
     */
    private void export() {
        //查询所有数据
        AnalyzeDao dao = new AnalyzeDao(AnalyzePoemActivity.this);
        final List<AnalyzeBean> list = dao.queryAll();

        if (list == null || list.size() == 0) {
            Toast.makeText(this, "查询不到数据", Toast.LENGTH_LONG).show();
            return;
        }

        //显示一个输入导出的文件名的对话框
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_export, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.setContentView(view);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        final EditText editText = (EditText) view.findViewById(R.id.editText);

        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        Button btn_commit = (Button) view.findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showProgress("正在导出，请稍后......");

                String name = editText.getText().toString();
                Log.e(TAG, "name=" + name);
                if (TextUtils.isEmpty(name)) {
                    name = editText.getHint().toString();
                    name = name.substring(5, name.length());
                }

                final String finalName = name;

                //获取SDCard路径
                final File file = Environment.getExternalStorageDirectory();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String text = "";
                        for (int i = 0; i < list.size(); i++) {
                            text += (list.get(i).getAmount() + "," + list.get(i).getName() + "\n");
                        }
                        //保存文件
                        FileUtils.saveTxt(text, AnalyzePoemActivity.this, file.getAbsolutePath() + File.separator + "Download", finalName + ".csv");
                        exportFinishHandler.sendEmptyMessage(0);
                    }
                }).start();

            }
        });

    }

    /**
     * 单字更新UI
     */
    @Override
    public void setLoadWorsdUpdateUI(String text) {
        Message message = new Message();
        message.obj = text;
        loadWorsUIHandler.sendMessage(message);
    }

    /**
     * 单字更新UI
     */
    private final Handler loadWorsUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mTxtContent.setText(String.valueOf(msg.obj) + "%");
        }
    };

    /**
     * 清空数据结束更新UI
     */
    private final Handler clearFinishUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissProgress();
        }
    };

    /**
     * 清空数据
     */
    private void clear() {
        showProgress("正在清空，请稍后......");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //清空数据库
                new AnalyzeDao(AnalyzePoemActivity.this).deletedAll();
                Log.e(TAG, "清空完毕");
                clearFinishUIHandler.sendEmptyMessage(0);
            }
        }).start();
        //清空内容
        mTxtContent.setText("");
    }

    /**
     * 导入数据完成UI更新
     */
    private final Handler inportFinishUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissProgress();
            Toast.makeText(AnalyzePoemActivity.this, "导入成功", Toast.LENGTH_SHORT).show();
            //展示数据
            showData();
        }
    };

    /**
     * 导入数据
     */
    private void inport() {
        DialogSelectionListener listener = new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(final String[] files) {
                if (files != null && files.length >= 0) {

                    showProgress("正在导入，请稍后......");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //读取文件
                            List<AnalyzeBean> list = FileUtils.readCSV(files[0]);

                            AnalyzeDao dao = new AnalyzeDao(AnalyzePoemActivity.this);
                            //逐个检查合并数据
                            for (int i = 0; i < list.size(); i++) {
                                dao.merge(list.get(i));
                            }

                            Log.e(TAG, "导入完毕");
                            inportFinishUIHandler.sendEmptyMessage(0);
                        }
                    }).start();

                }
            }
        };
        openFile(listener);

    }

    @Override
    @OnClick({R.id.button_terms, R.id.button_word, R.id.button_export, R.id.button_inport, R.id.button_clear})
    public void onClick(View view) {
        if (view.getId() == R.id.button_terms) {
            parseTerms();
        } else if (view.getId() == R.id.button_word) {
            parseWords();
        } else if (view.getId() == R.id.button_export) {
            export();
        } else if (view.getId() == R.id.button_inport) {
            inport();
        } else if (view.getId() == R.id.button_clear) {
            clear();
        }
    }


}
