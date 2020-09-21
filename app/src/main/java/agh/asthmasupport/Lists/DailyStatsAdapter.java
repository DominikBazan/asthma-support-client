package agh.asthmasupport.Lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import agh.asthmasupport.R;
import agh.asthmasupport.communication.objects.DailyStatistics;

public class DailyStatsAdapter extends ArrayAdapter<DailyStatistics> {

    private Context context;
    int resource;

    public DailyStatsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<DailyStatistics> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String date = getItem(position).getDate();
        String points = getItem(position).getValue();
        String implemented = getItem(position).getImplemented();
        String rain = getItem(position).getRain();
        String wind = getItem(position).getWind();
        String temperature = getItem(position).getTemperature();
        String dusting = getItem(position).getDusting();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView tvDate = (TextView) convertView.findViewById(R.id.date_content);
        TextView tvPoints = (TextView) convertView.findViewById(R.id.points_content);
        TextView tvImplemented = (TextView) convertView.findViewById(R.id.implemented_content);
        TextView tvRain = (TextView) convertView.findViewById(R.id.rain_content);
        TextView tvWind = (TextView) convertView.findViewById(R.id.wind_content);
        TextView tvTemperature = (TextView) convertView.findViewById(R.id.temperature_content);
        TextView tvDusting = (TextView) convertView.findViewById(R.id.dusting_content);

        tvDate.setText(date);
        tvPoints.setText(points);
        tvImplemented.setText(implemented);
        tvRain.setText(rain);
        tvWind.setText(wind);
        tvTemperature.setText(temperature);
        tvDusting.setText(dusting);

        return convertView;
    }
}
