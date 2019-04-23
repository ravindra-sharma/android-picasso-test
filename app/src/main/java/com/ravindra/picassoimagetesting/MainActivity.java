package com.ravindra.picassoimagetesting;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> imageurls;
    CustomAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.list_view);
        imageurls=new ArrayList<>();
        new Background(this).execute();
        customAdapter=new CustomAdapter(this,R.layout.image,imageurls);
        listView.setAdapter(customAdapter);


    }
    private class Background extends AsyncTask<Void,Void,String>
    {
        ProgressDialog progressDialog;
        Context context;
        public Background(Context context)
        {
         this.context=context;
        }
        @Override
        protected void onPreExecute() {
            Toast.makeText(getBaseContext(),"image loading start",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String imageapi="http://192.168.42.96/img/imagesend.php";
            try {
                URL url=new URL(imageapi);
                HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line="";
                String result="";
                while ((line=bufferedReader.readLine())!=null)
                {
                    result+=line;
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject object=new JSONObject(result);
                JSONArray jsonArray=object.getJSONArray("result");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    imageurls.add(jsonObject.getString("image"));
                }
                Toast.makeText(getBaseContext(),"image loading complete",Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class CustomAdapter extends ArrayAdapter<String> {
        ArrayList<String> list;
        Context ctx;
        public CustomAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
            ctx=context;
            list=objects;
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v= LayoutInflater.from(ctx).inflate(R.layout.image,parent,false);
            ImageView imageView=(ImageView)v.findViewById(R.id.imageView);
            TextView textView=(TextView)v.findViewById(R.id.textview);
            String url=getItem(position);
            textView.setText(url);
            Picasso.with(ctx)
                    .load(url)
                    .resize(100, 100)
                    .centerCrop()
                    .placeholder(getResources().getDrawable(R.mipmap.ic_launcher))
                    .into(imageView);
            return v;
        }
    }
}
