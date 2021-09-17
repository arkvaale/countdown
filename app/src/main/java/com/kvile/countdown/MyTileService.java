package com.kvile.countdown;

import androidx.annotation.NonNull;
import androidx.wear.tiles.LayoutElementBuilders;
import androidx.wear.tiles.RequestBuilders;
import androidx.wear.tiles.ResourceBuilders;
import androidx.wear.tiles.TileBuilders;
import androidx.wear.tiles.TileService;
import androidx.wear.tiles.TimelineBuilders;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class MyTileService extends TileService {
    private static final String RESOURCES_VERSION = "1";

    @NonNull
    @Override
    protected ListenableFuture<TileBuilders.Tile> onTileRequest(
            @NonNull RequestBuilders.TileRequest requestParams
    ) {
        TileBuilders.Tile tile = new TileBuilders.Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTimeline(
                        new TimelineBuilders.Timeline.Builder()
                                // We add a single timeline entry when our layout is fixed, and
                                // we don't know in advance when its contents might change.
                                .addTimelineEntry(new TimelineBuilders.TimelineEntry.Builder().build()
                                        //.setLayout(getLayout()).build()
                                ).build()
                ).build();
        return Futures.immediateFuture(tile);
    }

    private LayoutElementBuilders.Layout getLayout() {
        return null;
    }

    @NonNull
    @Override
    protected ListenableFuture<ResourceBuilders.Resources> onResourcesRequest(
            @NonNull RequestBuilders.ResourcesRequest requestParams
    ) {
        return Futures.immediateFuture(new ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build()
        );
    }
}
