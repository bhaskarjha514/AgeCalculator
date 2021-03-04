package com.androidllc.agecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private TextView currentDateTv,dobTv,rDay, rMonth, rYear,nextMonthTv, nextDateTv;
    private RelativeLayout chooseBirthDateRl, resultRl;

    private Calendar dobCalendar;
    private int cDate, cMonth, cYear;
    private int dobDate, dobMonth, dobYear;
    private  int resultYear, resultMonth, resultDate;

    private SimpleDateFormat yearFormat, monthFormat, dateFormat;

    private MaterialDatePicker materialDatePicker;
    private MaterialDatePicker.Builder builder;
    private Button calculateBtn;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sdf = new SimpleDateFormat("dd-MM-yyyy");
        yearFormat = new SimpleDateFormat("yyyy");
        monthFormat = new SimpleDateFormat("MM");
        dateFormat = new SimpleDateFormat("dd");

        bindID();
        fetchCurrentDate();
        dateSetting();
        chooseBirthDateRl.setOnClickListener(v->chooseBirthDateRl());
        calculateBtn.setOnClickListener(v->giveResult());
    }

    private void giveResult() {
        if (!dobTv.getText().toString().isEmpty()){
            resultRl.setVisibility(View.VISIBLE);
            calculateYear();
            calculateMonth();
            calculateDay();
            int remainingDay = calculateRemainingDays(dobMonth,dobDate);
            if (remainingDay<=30){
                nextMonthTv.setText("0");
                nextDateTv.setText(String.valueOf(remainingDay));
            }else{
                int dateLeft = remainingDay%30;
                int leftMonth = remainingDay/30;
                nextMonthTv.setText(String.valueOf(leftMonth));
                nextDateTv.setText(String.valueOf(dateLeft));
            }
            Log.d("NextBirthDay",String.valueOf(calculateRemainingDays(dobMonth,dobDate)));
            rDay.setText(String.valueOf(resultDate));
            rMonth.setText(String.valueOf(resultMonth));
            rYear.setText(String.valueOf(resultYear));

        }else{
            Toast.makeText(this, "Choose Birth Day first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void dateSetting() {
        dobCalendar = Calendar.getInstance();
        long today = MaterialDatePicker.todayInUtcMilliseconds();

        dobCalendar.clear();
        dobCalendar.setTimeInMillis(today);

        dobCalendar.set(Calendar.YEAR,cYear);

        dobCalendar.set(Calendar.MONTH, cMonth);
        long year2021 = dobCalendar.getTimeInMillis();


        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setEnd(year2021);
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());

        builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("CHOOSE YOUR BIRTHDATE");
        builder.setSelection(today);
        builder.setCalendarConstraints(constraintsBuilder.build());
        materialDatePicker = builder.build();

    }

    private void chooseBirthDateRl() {
        materialDatePicker.show(getSupportFragmentManager(),"Choose BirthDate");
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                dobDate = Integer.parseInt(dateFormat.format(selection));
                dobMonth = Integer.parseInt(monthFormat.format(selection));
                dobYear = Integer.parseInt(yearFormat.format(selection));
                dobTv.setText(dobDate+"/"+dobMonth+"/"+dobYear);
            }
        });


    }

    private void fetchCurrentDate() {
        SimpleDateFormat formatter  = new SimpleDateFormat("dd / MM / yyyy");
        Date date = new Date();
        currentDateTv.setText(formatter.format(date));

        cDate = Integer.parseInt(dateFormat.format(date));
        cMonth = Integer.parseInt(monthFormat.format(date));
        cYear = Integer.parseInt(yearFormat.format(date));
    }

    private void bindID() {
        currentDateTv = findViewById(R.id.todayDateTv);
        chooseBirthDateRl = findViewById(R.id.dobRl);
        dobTv = findViewById(R.id.dobDateTv);
        calculateBtn = findViewById(R.id.calculateBtn);
        resultRl = findViewById(R.id.resultRl);

        rDay = findViewById(R.id.daysTv);
        rMonth = findViewById(R.id.monthsTv);
        rYear = findViewById(R.id.yearsTv);

        nextMonthTv = findViewById(R.id.nextMonthsTv);
        nextDateTv = findViewById(R.id.nextDayTv);
    }

    public void calculateYear() {
        resultYear = cYear - dobYear;
    }
    public void calculateMonth() {
        if (cMonth >= dobMonth) {
            resultMonth = cMonth - dobMonth;
        } else {
            resultMonth = cMonth - dobMonth;
            resultMonth = 12 + resultMonth;
            resultYear--;
        }
    }
    public void calculateDay() {
        if (cDate >= dobDate) resultDate = cDate - dobDate;
        else {
            resultDate = cDate - dobDate;
            resultDate = 30 + resultDate;
            if (resultMonth == 0) {
                resultMonth = 11;
                resultYear--;
            } else resultMonth--;
        }
    }
    public int calculateRemainingDays(int selectedMonth, int selectedDay) {

        Date date1 = new Date();
        date1.setDate(selectedDay);
        date1.setMonth(selectedMonth);

        Date date2 = new Date();
        date2.setDate(cDate);
        date2.setMonth(cMonth);

        // find the difference between two dates
        long difference = date1.getTime() - date2.getTime();

        int remainingDays = (int) TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);

        Log.d("TAG", String.valueOf(remainingDays));
        if (remainingDays >= 365) {
            remainingDays = remainingDays - 365;
        } else if (remainingDays < 0)
            remainingDays = 365 - Math.abs(remainingDays);

        return remainingDays;
    }
}