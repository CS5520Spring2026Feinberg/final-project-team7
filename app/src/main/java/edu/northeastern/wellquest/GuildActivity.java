package edu.northeastern.wellquest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.wellquest.helpers.GameHelper;
import edu.northeastern.wellquest.models.Guild;
import edu.northeastern.wellquest.models.Player;

public class GuildActivity extends AppCompatActivity {

    private LinearLayout llNoGuild, llHasGuild;
    private EditText etGuildName;
    private Button btnCreateGuild, btnJoinGuild, btnLeaveGuild;

    private TextView tvActiveGuildName, tvGuildId, tvBossHealthText;
    private ProgressBar pbBossHealth;
    private RecyclerView rvLeaderboard;
    private LeaderboardAdapter adapter;

    private DatabaseReference mDatabase;
    private String currentGuildId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guild);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        llNoGuild = findViewById(R.id.ll_no_guild);
        llHasGuild = findViewById(R.id.ll_has_guild);
        etGuildName = findViewById(R.id.et_guild_name);
        btnCreateGuild = findViewById(R.id.btn_create_guild);
        btnJoinGuild = findViewById(R.id.btn_join_guild);
        btnLeaveGuild = findViewById(R.id.btn_leave_guild);

        tvActiveGuildName = findViewById(R.id.tv_active_guild_name);
        tvGuildId = findViewById(R.id.tv_guild_id);
        tvBossHealthText = findViewById(R.id.tv_boss_health_text);
        pbBossHealth = findViewById(R.id.pb_boss_health);

        rvLeaderboard = findViewById(R.id.rv_leaderboard);
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LeaderboardAdapter();
        rvLeaderboard.setAdapter(adapter);

        btnCreateGuild.setOnClickListener(v -> {
            String name = etGuildName.getText().toString().trim();
            if (!name.isEmpty()) {
                GameHelper.createGuild(name);
                Toast.makeText(this, "Guild Created!", Toast.LENGTH_SHORT).show();
            }
        });

        btnJoinGuild.setOnClickListener(v -> {
            String id = etGuildName.getText().toString().trim();
            if (!id.isEmpty()) {
                GameHelper.joinGuild(id);
                Toast.makeText(this, "Joined Guild!", Toast.LENGTH_SHORT).show();
            }
        });

        btnLeaveGuild.setOnClickListener(v -> {
            if (!currentGuildId.isEmpty()) {
                GameHelper.leaveGuild(currentGuildId);
                Toast.makeText(this, "Left Guild!", Toast.LENGTH_SHORT).show();
            }
        });

        checkUserGuildStatus();
    }

    private void checkUserGuildStatus() {
        DatabaseReference userRef = GameHelper.getUserRef();
        if (userRef == null) return;

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                Player player = snapshot.getValue(Player.class);
                if (player != null && player.getGuildId() != null && !player.getGuildId().isEmpty()) {
                    currentGuildId = player.getGuildId();
                    showHasGuildUI();
                    loadGuildData(currentGuildId);
                } else {
                    currentGuildId = "";
                    showNoGuildUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showNoGuildUI() {
        llNoGuild.setVisibility(View.VISIBLE);
        llHasGuild.setVisibility(View.GONE);
    }

    private void showHasGuildUI() {
        llNoGuild.setVisibility(View.GONE);
        llHasGuild.setVisibility(View.VISIBLE);
    }

    private void loadGuildData(String guildId) {
        mDatabase.child("guilds").child(guildId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                Guild guild = snapshot.getValue(Guild.class);
                if (guild != null) {
                    tvActiveGuildName.setText(guild.getName());
                    tvGuildId.setText("ID: " + guild.getId());

                    int hp = guild.getBossHealth() - guild.getCurrentGuildDamage();
                    hp = Math.max(0, hp);
                    pbBossHealth.setMax(guild.getBossMaxHealth());
                    pbBossHealth.setProgress(hp);
                    tvBossHealthText.setText(hp + " / " + guild.getBossMaxHealth() + " HP");

                    loadGuildMembers(guild);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadGuildMembers(Guild guild) {
        if (guild.getMembers() == null || guild.getMembers().isEmpty()) return;

        List<Player> memberList = new ArrayList<>();
        for (String memberId : guild.getMembers().keySet()) {
            mDatabase.child("users").child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Player p = snapshot.getValue(Player.class);
                        if (p != null) {
                            memberList.add(p);
                            adapter.setMembers(memberList);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}