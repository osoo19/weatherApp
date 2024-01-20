package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class WeatherAdapter extends BaseAdapter {
    private Context context;
    private String[] weatherData;

    public WeatherAdapter(Context context, String[] weatherData) {
        this.context = context;
        this.weatherData = weatherData;
    }

    @Override
    public int getCount() {
        return weatherData.length;
    }

    @Override
    public Object getItem(int position) {
        return weatherData[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // カスタムのリストアイテムレイアウトをインフレート
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_list_item, parent, false);
        }

        // アイコン画像を表示する条件分岐
        ImageView iconImageView = convertView.findViewById(R.id.iconImageView);
        String iconUrl = getIconUrlFromData(position);

        if (position % 3 == 0 && !iconUrl.isEmpty()) {
            // アイコンが表示される場合
            loadIconWithGlide(iconImageView, iconUrl);
            iconImageView.setVisibility(View.VISIBLE);

            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText("");
        } else {
            // アイコンが表示されない場合
            iconImageView.setVisibility(View.GONE);
            // その他のデータの表示
            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText(weatherData[position]);
        }



        return convertView;
    }

    // 実際のデータからアイコンURLを取得するメソッド
    private String getIconUrlFromData(int position) {
        String[] data = weatherData[position].split(",");

        if (data.length > 0) {
            String icon = data[0].trim();  // アイコンの文字列
            return context.getString(R.string.weather_icon_url) + "/" + icon + "@2x.png";
        }

        // アイコンの文字列が取得できない場合は、空の文字列を返す
        return "";
    }

    // Glide ライブラリを使用してアイコンを読み込むメソッド
    private void loadIconWithGlide(ImageView imageView, String iconUrl) {
        if (!iconUrl.isEmpty()) {
            Glide.with(context)
                    .load(iconUrl)
                    .apply(new RequestOptions().dontAnimate())
                    .into(imageView);
        } else {
            // アイコンが空の場合は何も表示しない
            imageView.setImageDrawable(null);
        }
    }


}

