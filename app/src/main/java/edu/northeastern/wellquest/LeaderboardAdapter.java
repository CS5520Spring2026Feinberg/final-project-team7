package edu.northeastern.wellquest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.wellquest.models.Player;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<Player> members = new ArrayList<>();

    public void setMembers(List<Player> members) {
        this.members = members;
        // Simple sort by total steps descending
        this.members.sort((p1, p2) -> Integer.compare(p2.getTotalSteps(), p1.getTotalSteps()));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = members.get(position);
        holder.tvName.setText(player.getUsername());
        holder.tvLevel.setText("Lvl " + player.getLevel());
        holder.tvSteps.setText("Steps: " + player.getTotalSteps());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLevel, tvSteps;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_member_name);
            tvLevel = itemView.findViewById(R.id.tv_member_level);
            tvSteps = itemView.findViewById(R.id.tv_member_steps);
        }
    }
}