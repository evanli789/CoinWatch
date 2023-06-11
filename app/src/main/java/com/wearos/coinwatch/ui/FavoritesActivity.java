package com.wearos.coinwatch.ui;

import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.SnapHelper;
import androidx.wear.widget.WearableLinearLayoutManager;

import com.wearos.coinwatch.R;
import com.wearos.coinwatch.adapter.FavoritesAdapter;
import com.wearos.coinwatch.util.CustomScroll;
import com.wearos.coinwatch.viewmodels.FavoritesViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoritesActivity extends ComponentActivity implements FavoritesAdapter.ItemClickCallback {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FavoritesAdapter adapter;
    private FavoritesViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_activity);
        ButterKnife.bind(this);

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        adapter = new FavoritesAdapter(viewModel.getItemList(), viewModel.getMapFavoriteCoins(), this);
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this, new CustomScroll()));

        //Disable notifyItemChanged animation because it disables checkbox animation
        SimpleItemAnimator simpleItemAnimator = (SimpleItemAnimator) recyclerView.getItemAnimator();
        if (simpleItemAnimator != null) {
            simpleItemAnimator.setSupportsChangeAnimations(false);
        }

        recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        viewModel.getLiveData().observe(this, state -> {
            switch (state.getType()) {
                case SUCCESS_GET_FAVORITES:
                    adapter.notifyDataSetChanged();
                    break;

                case ON_FAVORITE_CLICKED:
                    adapter.notifyItemChanged(state.getAdapterPosition());
                    break;
            }
        });
        viewModel.getFavorites();
    }

    @Override
    public void onFavoritesClick(int adapterPosition) {
        viewModel.onFavoriteItemClick(adapterPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.dispose();
    }
}
