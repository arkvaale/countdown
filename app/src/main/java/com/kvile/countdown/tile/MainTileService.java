package com.kvile.countdown.tile;

import static androidx.wear.tiles.DimensionBuilders.dp;
import static androidx.wear.tiles.DimensionBuilders.expand;
import static androidx.wear.tiles.DimensionBuilders.sp;
import static androidx.wear.tiles.DimensionBuilders.wrap;
import static com.kvile.countdown.common.CountdownsAdapter.getTextColor;

import androidx.annotation.NonNull;
import androidx.wear.tiles.ActionBuilders;
import androidx.wear.tiles.ColorBuilders;
import androidx.wear.tiles.LayoutElementBuilders;
import androidx.wear.tiles.ModifiersBuilders;
import androidx.wear.tiles.RequestBuilders;
import androidx.wear.tiles.ResourceBuilders;
import androidx.wear.tiles.TileBuilders;
import androidx.wear.tiles.TileService;
import androidx.wear.tiles.TimelineBuilders;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.kvile.countdown.MainActivity;
import com.kvile.countdown.common.Countdown;
import com.kvile.countdown.common.CountdownDataApplication;

import java.util.List;

public class MainTileService extends TileService {

    private static final String RESOURCES_VERSION = "1";
    private final CountdownDataApplication countdownDataApplication = new CountdownDataApplication(this);

    @NonNull
    @Override
    protected ListenableFuture<TileBuilders.Tile> onTileRequest(@NonNull RequestBuilders.TileRequest requestParams) {
        TimelineBuilders.Timeline.Builder timeline = new TimelineBuilders.Timeline.Builder();

        LayoutElementBuilders.Column.Builder layoutColumn = new LayoutElementBuilders.Column.Builder();
        layoutColumn
                .setWidth(wrap())
                .setHeight(expand())
                .setModifiers(new ModifiersBuilders.Modifiers.Builder()
                        .setClickable(getOpenMainActivityClickable())
                        .setSemantics(new ModifiersBuilders.Semantics.Builder()
                                .setContentDescription("List of countdowns")
                                .build())
                        .setPadding(new ModifiersBuilders.Padding.Builder()
                                .setTop(dp(8f))
                                .setBottom(dp(8f))
                                .build())
                        .build())
                .build();
        List<Countdown> countdowns = countdownDataApplication.getList();
        if (!countdowns.isEmpty()) {
            countdowns.stream()
                    .filter(c -> c.getDaysRemaining() >= 0)
                    .limit(3)
                    .forEach(c -> layoutColumn.addContent(getCountdownLayout(c)));
        } else {
            layoutColumn.addContent(getTextLayoutRow("No countdowns"));
            layoutColumn.addContent(getTextLayoutRow("to be displayed."));
            layoutColumn.addContent(getTextLayoutRow("Click here to add"));
            layoutColumn.addContent(getTextLayoutRow("in the app."));
        }
        layoutColumn.addContent(getHeightSpacer(10f));
        layoutColumn.addContent(getRefreshTileElement());
        layoutColumn.addContent(getHeightSpacer(5f));
        timeline.addTimelineEntry(new TimelineBuilders.TimelineEntry.Builder()
                .setLayout(new LayoutElementBuilders.Layout.Builder()
                        .setRoot(layoutColumn.build())
                        .build())
                .build());

        TileBuilders.Tile tile = new TileBuilders.Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setFreshnessIntervalMillis(6 * 60 * 60 * 1000) // 6 hours
                .setTimeline(timeline.build())
                .build();
        return Futures.immediateFuture(tile);
    }

    private LayoutElementBuilders.LayoutElement getTextLayoutRow(String text) {
        return new LayoutElementBuilders.Row.Builder()
                .setWidth(wrap())
                .setHeight(expand())
                .addContent(new LayoutElementBuilders.Text.Builder()
                        .setText(text)
                        .build())
                .build();
    }

    private LayoutElementBuilders.LayoutElement getCountdownLayout(Countdown countdown) {
        return new LayoutElementBuilders.Row.Builder()
                .setWidth(wrap())
                .setHeight(expand())
                .addContent(new LayoutElementBuilders.Text.Builder()
                        .setText(countdown.getName())
                        .setFontStyle(new LayoutElementBuilders.FontStyle.Builder()
                                .setColor(new ColorBuilders.ColorProp.Builder()
                                        .setArgb(getTextColor(countdown.getDaysRemaining()))
                                        .build())
                                .setSize(sp(16))
                                .build())
                        .build()
                )
                .addContent(new LayoutElementBuilders.Spacer.Builder()
                        .setWidth(dp(12f))
                        .build())
                .addContent(new LayoutElementBuilders.Text.Builder()
                        .setText(countdown.getDaysRemaining() <= 0 ? "\u2713" : String.valueOf(countdown.getDaysRemaining()))
                        .setFontStyle(new LayoutElementBuilders.FontStyle.Builder()
                                .setColor(new ColorBuilders.ColorProp.Builder()
                                        .setArgb(getTextColor(countdown.getDaysRemaining()))
                                        .build())
                                .build())
                        .build())
                .build();
    }

    private LayoutElementBuilders.Spacer getHeightSpacer(float size) {
        return new LayoutElementBuilders.Spacer.Builder()
                .setHeight(dp(size))
                .build();
    }

    private ModifiersBuilders.Clickable getOpenMainActivityClickable() {
        return new ModifiersBuilders.Clickable.Builder()
                .setId("openMainActivity")
                .setOnClick(new ActionBuilders.LaunchAction.Builder()
                        .setAndroidActivity(new ActionBuilders.AndroidActivity.Builder()
                                .setClassName(MainActivity.class.getName())
                                .setPackageName(this.getPackageName())
                                .build())
                        .build())
                .build();
    }

    private LayoutElementBuilders.LayoutElement getRefreshTileElement() {
        return new LayoutElementBuilders.Text.Builder()
                .setText("\u21bb")
                .setFontStyle(new LayoutElementBuilders.FontStyle.Builder()
                        .setSize(sp(25))
                        .build())
                .setModifiers(new ModifiersBuilders.Modifiers.Builder()
                        .setClickable(new ModifiersBuilders.Clickable.Builder()
                                .setId("refreshTile")
                                .setOnClick(new ActionBuilders.LoadAction.Builder().build())
                                .build())
                        .build())
                .build();
    }

    @NonNull
    @Override
    protected ListenableFuture<ResourceBuilders.Resources> onResourcesRequest(@NonNull RequestBuilders.ResourcesRequest requestParams) {
        return Futures.immediateFuture(new ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build()
        );
    }

}
