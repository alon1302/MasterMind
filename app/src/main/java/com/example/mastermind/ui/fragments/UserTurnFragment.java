package com.example.mastermind.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.game.CheckRow;
import com.example.mastermind.model.game.GameManager;
import com.example.mastermind.model.game.GamePeg;
import com.example.mastermind.model.game.GameRow;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.listeners.OnPegClickListener;
import com.example.mastermind.model.serviceAndBroadcast.BackMusicService;
import com.example.mastermind.model.theme.Themes;
import com.example.mastermind.model.user.User;
import com.example.mastermind.ui.adapters.AdapterRows;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserTurnFragment extends Fragment implements OnPegClickListener {

    private final GameManager gameManager = new GameManager();;

    private ArrayList<GameRow> gameRows;
    private ArrayList<CheckRow> checkRows;
    private GameRow hiddenRow;

    private CircleImageView red, green, blue, orange, yellow, light;
    private CircleImageView current;
    private String currentSelection = Const.NULL_COLOR_IN_GAME;

    private AdapterRows adapterRows;
    private RecyclerView recyclerView;

    private User user1;
    private String player;

    private View view;
    private Context context;
    private Drawable theme;

    private Intent service;
    private ImageView iv_musicOnOff;
    private boolean isPlaying;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        user1 = (User) bundle.get(Const.INTENT_EXTRA_KEY_USER1);
        player = bundle.getString(Const.INTENT_EXTRA_KEY_PLAYER);
        String code = bundle.getString(Const.INTENT_EXTRA_KEY_CODE);

        int useIndex = Themes.getInstance(requireActivity().getApplicationContext()).getCurrentThemeIndex();
        int themeImg = Themes.getInstance(requireActivity().getApplicationContext()).getAllThemes().get(useIndex).getPegImage();
        theme = this.getResources().getDrawable(themeImg);

        String opponent;
        if (player.equals(Const.PLAYER1))
            opponent = Const.PLAYER2;
        else
            opponent = Const.PLAYER1;
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.GAME_IN_FIREBASE).child(opponent + Const.HIDDEN_IN_FIREBASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    hiddenRow= new GameRow();
                    String row = snapshot.getValue(String.class);
                    for (int i = 0; i < Const.ROW_SIZE; i++)
                        hiddenRow.addPeg(new GamePeg((String) Const.CHAR_TO_STRING_MAP.get(row.charAt(i)), i));
                    gameManager.setHidden(hiddenRow);
                    createHidden();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_turn, container, false);
        context = requireActivity();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.user_multi_recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        gameRows = gameManager.getGameRows();
        checkRows = gameManager.getCheckRows();
        adapterRows = new AdapterRows(gameRows, checkRows,context,true);
        recyclerView.setAdapter(adapterRows);
        //createHidden();
        createButtons();

        Glide.with(context).load(user1.getImgUrl()).into((CircleImageView)view.findViewById(R.id.user_multi_img));
        view.findViewById(R.id.user_multi_btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubmit();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        service = new Intent(context.getApplicationContext(), BackMusicService.class);
        iv_musicOnOff = view.findViewById(R.id.btn_Music);
        if (BackMusicService.isPlaying){
            isPlaying = true;
            iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_off_24);
        } else {
            isPlaying = false;
            iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_note_24);
        }
        iv_musicOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying){
                    context.startService(service);
                    iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_off_24);
                    isPlaying = true;
                } else {
                    context.stopService(service);
                    iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_note_24);
                    isPlaying = false;
                }
            }
        });
    }

    public void createHidden() {
        CircleImageView[] hiddenRowImages = new CircleImageView[Const.ROW_SIZE];
        hiddenRowImages[0] = view.findViewById(R.id.user_multi_hidden0);
        hiddenRowImages[1] = view.findViewById(R.id.user_multi_hidden1);
        hiddenRowImages[2] = view.findViewById(R.id.user_multi_hidden2);
        hiddenRowImages[3] = view.findViewById(R.id.user_multi_hidden3);
        String[] hiddenColors = hiddenRow.getStringRow();
        for (int i = 0; i < hiddenColors.length; i++) {
            hiddenRowImages[i].setImageResource((Integer) Const.STRING_TO_COLOR_MAP.get(hiddenColors[i]));
            hiddenRowImages[i].setForeground(theme);
            hiddenRowImages[i].setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void createButtons() {
        red = view.findViewById(R.id.user_multi_red);
        red.setOnTouchListener(onClickColorListener);
        red.setForeground(theme);
        green = view.findViewById(R.id.user_multi_green);
        green.setOnTouchListener(onClickColorListener);
        green.setForeground(theme);
        blue = view.findViewById(R.id.user_multi_blue);
        blue.setOnTouchListener(onClickColorListener);
        blue.setForeground(theme);
        orange = view.findViewById(R.id.user_multi_orange);
        orange.setOnTouchListener(onClickColorListener);
        orange.setForeground(theme);
        yellow = view.findViewById(R.id.user_multi_yellow);
        yellow.setOnTouchListener(onClickColorListener);
        yellow.setForeground(theme);
        light = view.findViewById(R.id.user_multi_light);
        light.setOnTouchListener(onClickColorListener);
        light.setForeground(theme);
        current = view.findViewById(R.id.user_multi_currentSelection);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelection = Const.NULL_COLOR_IN_GAME;
                updateCurrImg();
            }
        });
    }

    View.OnTouchListener onClickColorListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v == red)
                currentSelection = Const.RED_COLOR_IN_GAME;
            else if (v == green)
                currentSelection = Const.GREEN_COLOR_IN_GAME;
            else if (v == blue)
                currentSelection = Const.BLUE_COLOR_IN_GAME;
            else if (v == orange)
                currentSelection = Const.ORANGE_COLOR_IN_GAME;
            else if (v == yellow)
                currentSelection = Const.YELLOW_COLOR_IN_GAME;
            else if (v == light)
                currentSelection = Const.LIGHT_COLOR_IN_GAME;
            updateCurrImg();
            return true;
        }
    };

    @Override
    public void onPositionClicked(int position) {
        String lastSelection = gameRows.get(gameManager.getTurn() - 1).getColorByPosition(position);
        gameManager.pegToGameRow(currentSelection, position);
        currentSelection = lastSelection;
        updateCurrImg();
        MethodCallBack methodCallBack = (MethodCallBack)requireActivity();
        methodCallBack.onCallBack(Const.ACTION_UPLOAD_ROW_CHANGES,gameRows.get(gameManager.getTurn()-1).getNumStringRow() + "|" + (gameManager.getTurn()-1));
        adapterRows.notifyDataSetChanged();
    }

    public void updateCurrImg() {
        current.setImageResource((Integer) Const.STRING_TO_COLOR_MAP.get(currentSelection));
        if (!currentSelection.equals(Const.NULL_COLOR_IN_GAME))
            current.setForeground(theme);
        else
            current.setForeground(null);
    }

    public void onClickSubmit() {
        if (gameRows.get(gameManager.getTurn() - 1).isFull()) {
            checkRows.add(gameManager.getTurn() - 1, gameRows.get(gameManager.getTurn() - 1).checkGameRow(hiddenRow));
            if (gameManager.isWin()){
                if (player.equals(Const.PLAYER2))
                    Toast.makeText(context, "You Win!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "You Win! but wait for the last turn", Toast.LENGTH_SHORT).show();
                MethodCallBack methodCallBack = (MethodCallBack)context;
                methodCallBack.onCallBack(Const.ACTION_WAITING_TO_WIN, null);
            }
            MethodCallBack methodCallBack = (MethodCallBack)context;
            methodCallBack.onCallBack(Const.ACTION_TURN_ROTATION, null);
            recyclerView.smoothScrollToPosition(adapterRows.getItemCount() - 1);
            adapterRows.notifyDataSetChanged();
        } else
            Toast.makeText(context, "Please make your guess", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!BackMusicService.isPlaying) {
            context.stopService(service);
        }
    }
}