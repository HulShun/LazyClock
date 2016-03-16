package com.example.lazyclock.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lazyclock.Config;
import com.example.lazyclock.bean.Mathematic;
import com.example.lazyclock.db.MathDatabaseHelper;
import com.example.lazyclock.others.CommonException;
import com.example.lazyclock.utils.HttpUtil;
import com.example.lazyclock.utils.LogUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Random;

/**
 * Created by MrShun on 2016/1/8.
 */
public class MathDao {

    private Context mContext;

    private static final String URLPATH = "http://www.tiku.cn";
    public static final String _ID = "_id";
    public static final String TOPIC = "topic";
    public static final String ANSWERA = "answerA";
    public static final String ANSWERB = "answerB";
    public static final String ANSWERC = "answerC";
    public static final String ANSWERD = "answerD";
    public static final String RIGHTANSWER = "rightAnswer";

    // 数据库版本号
    private static final int DATABASE_VERSION = 1;

    private MathDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDb;
    private HttpUtil mHttpUtil;


    /**
     * 获取答案的线程池
     */
    //  ExecutorService mPoolService;
    //  List<Runnable> mTaskQueue;
    //  Semaphore mPoolServiceSemaphore;
    /**
     * 任务队列的轮询线程
     */
    private Thread mPollThread;
    private boolean iswork = true;


    public MathDao(Context context) {
        mContext = context;
        String dbName = context.getExternalFilesDir(null).getPath() + "/" + Config.MATH_DATABASE_NAME;
        mDatabaseHelper = new MathDatabaseHelper(mContext, dbName, null, DATABASE_VERSION);
        mDb = mDatabaseHelper.getWritableDatabase();
        mHttpUtil = HttpUtil.getInstence();

     /*   mPoolService = Executors.newFixedThreadPool(5);
         mPoolServiceSemaphore = new Semaphore(5);
         mTaskQueue = new ArrayList<>();
                                                 f
        //线程池的轮询线程
        mPollThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (iswork) {
                    //如果任务队列不为空，就丢任务到线程池中
                    if (!mTaskQueue.isEmpty()) {
                        try {
                            mPoolServiceSemaphore.acquire();
                            mPoolService.execute(mTaskQueue.remove(0));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
        mPollThread.start();*/
    }


    /**
     * 网页爬虫 Jsoup获取String里的数据
     *
     * @param htmlString
     */
    public void insertDataFromStr(final String htmlString) {
        Document document = Jsoup.parse(htmlString);

        Elements elements = document.getElementsByTag("fieldset");
        for (int i = 0; i < elements.size(); i++) {
            final Mathematic mathematic = new Mathematic();
            Element element = elements.get(i);

            //题目
            Element element_pt1 = element.getElementsByClass("pt1").get(0);
            StringBuffer sb = new StringBuffer();
            Elements elements_uc = element_pt1.getElementsByClass("uc_q_object");
            for (int m = 0; m < elements_uc.size(); m++) {
                if (!elements_uc.get(m).children().isEmpty()) {
                    break;
                } else {
                    if (m % 2 == 0) {
                        sb.append(elements_uc.get(m).text());
                        if (m != elements_uc.size() - 1) {
                            sb.append("______");
                        }
                    }
                }
            }
            if (!sb.toString().equals("")) {
                mathematic.setTopic(sb.toString());
                //备选答案
                Elements element_pt2 = element.getElementsByClass("pt2");
                Elements answers = element_pt2.get(0).child(0).getElementsByTag("li");
                String str = answers.get(0).text();
                if (!str.equals("")) {
                    str = str.substring(2);
                    mathematic.setAnswerA(str);
                } else {
                    mathematic.setAnswerA(str);
                }

                str = answers.get(1).text();
                if (!str.equals("")) {
                    str = str.substring(2);
                    mathematic.setAnswerB(str);
                } else {
                    mathematic.setAnswerB(str);
                }

                str = answers.get(2).text();
                if (!str.equals("")) {
                    str = str.substring(2);
                    mathematic.setAnswerC(str);
                } else {
                    mathematic.setAnswerC(str);
                }

                str = answers.get(3).text();
                if (!str.equals("")) {
                    str = str.substring(2);
                    mathematic.setAnswerD(str);
                } else {
                    mathematic.setAnswerD(str);
                }


                //正确答案
                Elements element_fieldtip = element.getElementsByClass("fieldtip");
                final String path = URLPATH + element_fieldtip.get(0).child(0).attr("href");
                mathematic.setRightAnswer(path);
                if (mDb != null && mDb.isOpen()) {
                    ContentValues cv = new ContentValues();
                    cv.put("topic", mathematic.getTopic());
                    cv.put("answerA", mathematic.getAnswerA());
                    cv.put("answerB", mathematic.getAnswerB());
                    cv.put("answerC", mathematic.getAnswerC());
                    cv.put("answerD", mathematic.getAnswerD());
                    cv.put("rightAnswer", mathematic.getRightAnswer());
                    //插入数据
                    mDb.insert(Config.TABLE_NAME, null, cv);
                    LogUtil.i("db", "插入数学题到数据库成功！");
                    LogUtil.i("db", mathematic.getRightAnswer());
                }
            }

        }

    }


    public Cursor queryAll() {
        mDb = mDatabaseHelper.getReadableDatabase();
        String[] columns = {
                _ID,
                TOPIC,
                ANSWERA,
                ANSWERB,
                ANSWERC,
                ANSWERD,
                RIGHTANSWER,
        };
        String seleciton = _ID + " BETWEEN 1 AND 100 ";

        Cursor c = mDb.query(Config.TABLE_NAME, columns, seleciton, null, null, null, columns[0]);
        return c;
    }

    /**
     * 随机获取题目
     *
     * @return 数学题对象
     */
    public Mathematic getMathematicInRandom() {
        Cursor c = queryAll();
        if (c == null) {
            LogUtil.i("db", "没有获取到随机数学题，游标null");
            return null;
        }

        c.move(new Random().nextInt(99));
        Mathematic mathematic = new Mathematic();
        mathematic.setId(c.getInt(0));
        mathematic.setTopic(c.getString(1));
        mathematic.setAnswerA(c.getString(2));
        mathematic.setAnswerB(c.getString(3));
        mathematic.setAnswerC(c.getString(4));
        mathematic.setAnswerD(c.getString(5));
        mathematic.setRightAnswer(c.getString(6));

        return mathematic;
    }


    public void close() {
        mDatabaseHelper.close();
        iswork = false;
        LogUtil.i("db", "Math数据库已经关闭！");
    }

    /**
     * @param htmlString 网页的内容
     * @return
     * @throws CommonException
     */
    private String getRightAnswer(String htmlString) throws CommonException {
        Document document = Jsoup.parse(htmlString);
        String answer;
        Elements elements = document.getElementsByClass("pt5");
        answer = elements.text();
        //去除所有的空格
        answer = answer.replaceAll(" ", "");
        return answer;
    }


}
