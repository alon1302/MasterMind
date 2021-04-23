package com.example.mastermind.ui.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.example.mastermind.model.listeners.SendHiddenToOpponent;
import com.example.mastermind.model.serviceAndBroadcast.BackMusicService;
import com.example.mastermind.model.theme.Themes;
import com.example.mastermind.model.user.User;
import com.example.mastermind.ui.adapters.AdapterRows;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpponentTurnFragment extends Fragment implements SendHiddenToOpponent {

    User user1, user2;
    View view;

    RecyclerView recyclerView;
    AdapterRows adapterRows;

    GameManager gameManager;
    ArrayList<GameRow> gameRows;
    ArrayList<CheckRow> checkRows;
    String hidden;

    CircleImageView[] hiddenRowImages;

    String code, player;
    private Context context;

    ValueEventListener valueEventListener;
    private GameRow hiddenRow;

    private Drawable theme;
    private HashMap<String, Integer> colors;
    private HashMap<Character, String> charToColorMap;

    private ImageView iv_musicOnOff;
    private boolean playing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        user1 = (User) bundle.get(Const.INTENT_EXTRA_KEY_USER1);
        user2 = (User) bundle.get(Const.INTENT_EXTRA_KEY_USER2);
        code = bundle.getString(Const.INTENT_EXTRA_KEY_CODE);
        player = bundle.getString(Const.INTENT_EXTRA_KEY_PLAYER);

        int useIndex =Themes.getInstance(requireActivity().getApplicationContext()).getCurrentThemeIndex();
        int themeImg = Themes.getInstance(requireActivity().getApplicationContext()).getAllThemes().get(useIndex).getPegImage();
        theme = this.getResources().getDrawable(themeImg);
        createColorsMap();
        createCharToColorMap();

        gameManager = new GameManager();
        hiddenRow = new GameRow();
        for (int i = 0; i < 4; i++)
            hiddenRow.addPeg(new GamePeg(charToColorMap.get(hidden.charAt(i)), i));
        gameManager.setHidden(hiddenRow);
        gameManager = new GameManager();
        gameRows = gameManager.getGameRows();
        checkRows = gameManager.getCheckRows();
        hiddenRowImages = new CircleImageView[Const.ROW_SIZE];
        context = requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_opponent_turn, container, false);
        Glide.with(requireActivity()).load(user2.getImgUrl()).into((CircleImageView) view.findViewById(R.id.opponent_multi_img));
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView = view.findViewById(R.id.opponent_multi_recyclerView);
        adapterRows = new AdapterRows(gameRows, checkRows, requireActivity(), false);
        recyclerView.setAdapter(adapterRows);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.GAME_IN_FIREBASE).child(Const.TURNS_IN_FIREBASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    int turn = Integer.parseInt(ds.getKey());
                    String other = Const.PLAYER1;
                    if (player.equals(Const.PLAYER1))
                        other = Const.PLAYER2;
                    String row = ds.child(other).getValue(String.class);
                    if (gameRows.size() == turn)
                        gameRows.add(turn, convertStringToGameRow(row));
                    else
                        gameRows.set(turn, convertStringToGameRow(row));
                    checkRows.add(turn, gameRows.get(turn).checkGameRow(hiddenRow));
                    adapterRows.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        createRow();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        iv_musicOnOff = view.findViewById(R.id.btn_Music);
        if (isMyServiceRunning(BackMusicService.class)){
            playing = true;
            iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_off_24);
        } else {
            playing = false;
            iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_note_24);
        }
        iv_musicOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playing){
                    getActivity().startService(new Intent(getActivity(), BackMusicService.class));
                    iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_off_24);
                    playing = true;
                    Toast.makeText(getActivity(), "Service Start", Toast.LENGTH_SHORT).show();
                } else {
                    getActivity().stopService(new Intent(getActivity(), BackMusicService.class));
                    iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_note_24);
                    playing = false;
                    Toast.makeText(getActivity(), "Service Stop", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;
        return false;
    }

    public GameRow convertStringToGameRow(String row) {
        GameRow gameRow = new GameRow();
        if (row != null)
            for (int i = 0; i < Const.ROW_SIZE; i++)
                gameRow.addPeg(new GamePeg(charToColorMap.get(row.charAt(i)), i));
        return gameRow;
    }

    private void createRow() {
        hiddenRowImages[0] = view.findViewById(R.id.opponent_multi_hidden0);
        hiddenRowImages[1] = view.findViewById(R.id.opponent_multi_hidden1);
        hiddenRowImages[2] = view.findViewById(R.id.opponent_multi_hidden2);
        hiddenRowImages[3] = view.findViewById(R.id.opponent_multi_hidden3);

        String[] hiddenColors = hiddenRow.getStringRow();
        for (int i = 0; i < hiddenRowImages.length; i++) {
            hiddenRowImages[i].setClickable(false);
            this.hiddenRowImages[i].setForeground(theme);
            this.hiddenRowImages[i].setImageResource(colors.get(hiddenColors[i]));
        }
    }

    private void createCharToColorMap() {
        charToColorMap = new HashMap<>();
        charToColorMap.put(Const.NULL_CHAR_IN_GAME, Const.NULL_COLOR_IN_GAME);
        charToColorMap.put(Const.RED_CHAR_IN_GAME, Const.RED_COLOR_IN_GAME);
        charToColorMap.put(Const.GREEN_CHAR_IN_GAME, Const.GREEN_COLOR_IN_GAME);
        charToColorMap.put(Const.BLUE_CHAR_IN_GAME, Const.BLUE_COLOR_IN_GAME);
        charToColorMap.put(Const.ORANGE_CHAR_IN_GAME, Const.ORANGE_COLOR_IN_GAME);
        charToColorMap.put(Const.YELLOW_CHAR_IN_GAME, Const.YELLOW_COLOR_IN_GAME);
        charToColorMap.put(Const.LIGHT_CHAR_IN_GAME, Const.LIGHT_COLOR_IN_GAME);
    }

    public void createColorsMap() {
        colors = new HashMap<>();
        colors.put(Const.NULL_COLOR_IN_GAME, R.color.colorTWhite);
        colors.put(Const.RED_COLOR_IN_GAME, R.color.colorRed);
        colors.put(Const.GREEN_COLOR_IN_GAME, R.color.colorGreen);
        colors.put(Const.BLUE_COLOR_IN_GAME, R.color.colorBlue);
        colors.put(Const.ORANGE_COLOR_IN_GAME, R.color.colorOrange);
        colors.put(Const.YELLOW_COLOR_IN_GAME, R.color.colorYellow);
        colors.put(Const.LIGHT_COLOR_IN_GAME, R.color.colorLight);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void sendHidden(String hidden) {
        this.hidden = hidden;
    }
}