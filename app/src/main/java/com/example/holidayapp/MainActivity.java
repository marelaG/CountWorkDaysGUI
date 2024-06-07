package com.example.holidayapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button firstDatePickerButton;
    private Button secondDatePickerButton;
    private Button openSecondActivityButton;
    private TextView days;
    private TextView workingDays;
    private String dateStart;
    private String dateEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstDatePickerButton = findViewById(R.id.firstDatePickerButton);
        secondDatePickerButton = findViewById(R.id.secondDatePickerButton);
        days = findViewById(R.id.textView3);
        workingDays = findViewById(R.id.textView4);
        openSecondActivityButton = findViewById(R.id.openSecondActivityButton);

        firstDatePickerButton.setOnClickListener(this::openFirstDatePicker);
        secondDatePickerButton.setOnClickListener(this::openSecondDatePicker);
        openSecondActivityButton.setOnClickListener(view -> openSecondActivity());

    }

    public void openFirstDatePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view1, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                    firstDatePickerButton.setText(selectedDate);
                    dateStart = selectedDate;
                    calculateDays();
                },
                year, month, day);
        datePickerDialog.show();
    }

    public void openSecondDatePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view12, year12, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year12);
                    secondDatePickerButton.setText(selectedDate);
                    dateEnd = selectedDate;
                    calculateDays();
                },
                year, month, day);
        datePickerDialog.show();
    }
    public void openSecondActivity() {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }

    private void calculateDays() {
        if (dateStart != null && dateEnd != null) {
            try {
                int totalDays = countDays(dateStart, dateEnd);
                int totalWorkingDays = countWorkDays(dateStart, dateEnd);
                days.setText("Days between: "+String.valueOf(totalDays));
                workingDays.setText("Working days between: "+String.valueOf(totalWorkingDays));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static int countDays(String startS, String endS) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date start = sdf.parse(startS);
        Date end = sdf.parse(endS);
        long differenceInMillis = end.getTime() - start.getTime();
        long differenceInDays = differenceInMillis / (24 * 60 * 60 * 1000);
        return (int) differenceInDays;
    }

    public static int countWorkDays(String startS, String endS) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date start = sdf.parse(startS);
        Date end = sdf.parse(endS);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);

        int workingDays = 0;

        while (!calendar.equals(endCalendar)) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && !isHoliday(calendar)) {
                workingDays++;
            }
            calendar.add(Calendar.DATE, 1);
        }
        return workingDays;
    }

    public static boolean isHoliday(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH) + 1;  // Calendar.Month 0-11
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if ((month == 1 && day == 1) ||
                (month == 1 && day == 6) ||
                (month == 5 && day == 1) ||
                (month == 5 && day == 3) ||
                (month == 8 && day == 15) ||
                (month == 11 && day == 1) ||
                (month == 11 && day == 11) ||
                (month == 12 && day == 25) ||
                (month == 12 && day == 26) ||
                wielkanocBoze(calendar)) {
            return true;
        }
        return false;
    }

    public static boolean wielkanocBoze(Calendar calendar) {
        int rok = calendar.get(Calendar.YEAR);
        int a = rok % 19;
        int b = rok / 100;
        int c = rok % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int p = (h + l - 7 * m + 114) % 31;
        int dzień = p + 1;
        int miesiąc = (h + l - 7 * m + 114) / 31;


        Calendar easterSunday = Calendar.getInstance();
        easterSunday.set(rok, miesiąc - 1, dzień);
        Calendar easterMonday = (Calendar) easterSunday.clone();
        easterMonday.add(Calendar.DAY_OF_MONTH, 1);
        int day= easterMonday.get(Calendar.DAY_OF_MONTH); // is correct
        int Month=easterMonday.get(Calendar.MONTH); // is correct
        Calendar corpusChristi = (Calendar) easterSunday.clone();
        corpusChristi.add(Calendar.DAY_OF_MONTH, 60); // must be correct

        return (calendar.get(Calendar.DAY_OF_MONTH) == easterMonday.get(Calendar.DAY_OF_MONTH)) &&
                ((calendar.get(Calendar.MONTH) == easterMonday.get(Calendar.MONTH))) || (calendar.get(Calendar.DAY_OF_MONTH) == corpusChristi.get(Calendar.DAY_OF_MONTH) &&
                calendar.get(Calendar.MONTH) == corpusChristi.get(Calendar.MONTH));
    }

}
