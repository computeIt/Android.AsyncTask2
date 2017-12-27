package com.example.addy.asynctask2;
/*
* AsyncTask имеет 3 параметра, которые указываются в <par1, par2, par3>
* par1 - входные данные, подаваемые в метод execute и обрабатываемые в методе doInBackground
* par2 - промежуточные данные, подаваемые в метод publishProgress и обрабатываемые в методе onProgressUpdate
* par3 - итоговый тип данных, которые можно получить 2мя способами:
*                                               1)либо методом get на объекте MyTask(наследнике AsyncTask)
*                                               2)либо его же возвращает метод doInBackground
*                                               причем данные из doInBackground поступают на вход onPostExecute
* в данной реализации если нажать кнопку GET, не дожидаясь выполнения программы(точнее окончания работы метода doInBackground),
* то метод get подвесит выполнение основного потока и будет ждать его реализации,
* после чего все закончится благополучно и результат будет получен обоими методами
 * также есть реализация метода get с задаваемым timeout`ом
 * например result = mt.get(1, TimeUnit.SECONDS)
 * в этом случае метод подождет 1 секунду и если результат не будет получен, то бросит TimeoutException
* */

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    MyTask mt;
    Integer count = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);
    }

    public void onclick(View v) throws ExecutionException, InterruptedException {//пробрасываем исключение чтобы не загромождать код try-catch
        switch (v.getId()) {
            case R.id.button:
                mt = new MyTask();
                mt.execute(new MyFile("path1", 6),
                        new MyFile("path2", 7),
                        new MyFile("path3", 8),
                        new MyFile("path4", 9),
                        new MyFile("path5", 111));
                break;
            case R.id.button2:
                showResult();//попробуем получить результат с помощью метода get
                break;
            default:
                break;
        }
    }

    public class MyTask extends AsyncTask<MyFile, MyFile, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setText("begin");
        }

        @Override
        protected Integer doInBackground(MyFile... myFiles) {
            try {
                count = 0;
                for (MyFile myFile : myFiles) {
                    downloadFile(myFile);
                    publishProgress(myFile);//передает данные в метод onProgressUpdate
                    count++;//посчитаем кол-во обработанных файлов и вернем его в качестве результата работы метода
                }
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return count;
        }

        @Override
        protected void onProgressUpdate(MyFile... myFiles) {
            super.onProgressUpdate(myFiles);
            textView.setText("скачивается файд по имени " + myFiles[0].getName() + " c id = " + myFiles[0].getId());
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            textView.setText("end... полученные данные = " + count);

        }
    }
    
    private void downloadFile(MyFile myFile) throws InterruptedException {//иммитация сложной задачи
        TimeUnit.SECONDS.sleep(2);
    }

    private void showResult() throws ExecutionException, InterruptedException {//получение результатов с помощью метода get()
        Toast.makeText(this, "полученные данные с помощью get = " + mt.get(), Toast.LENGTH_LONG).show();
    }
    public class MyFile {//просто кастомный вспомогательный класс
        String name;
        Integer id;

        public MyFile(String name, Integer id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public Integer getId() {
            return id;
        }
    }
}



