package com.sky.medialib.ui.gallery;

import static com.sky.medialib.PictureEditActivityKt.PICK_PICTURE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.Glide;
import com.sky.media.image.core.util.BitmapUtil;
import com.sky.medialib.PictureEditActivity;
import com.sky.medialib.R;
import com.sky.medialib.ui.crop.VideoCropActivity;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.kit.common.view.RectImageView;
import com.sky.medialib.ui.kit.model.GalleryFolder;
import com.sky.medialib.ui.kit.model.GalleryModel;
import com.sky.medialib.util.EventBusHelper;
import com.sky.medialib.util.UIHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GalleryActivity extends AppActivity {
    public static final String KEY_FILTER_ID = "KEY_FILTER_ID";
    public static final String KEY_STICKER_ID = "KEY_STICKER_ID";
    public static final String KEY_TOPIC_NAME = "KEY_TOPIC_NAME";
    private GalleryAdapter mAdapter;
    private List<GalleryModel> mAllImages = new ArrayList();
    @BindView(R.id.back)
    TextView mBack;
    private List<GalleryModel> mCurrentImages = new ArrayList();
    private String mFilterId;
    private Map<String, GalleryFolder> mFilteredFolders = new LinkedHashMap();
    @BindView(R.id.folder_bg)
    ImageView mFolderBg;
    @BindView(R.id.folder_list)
    ListView mFolderList;
    private GalleryFolderAdapter mGalleryFolderAdapter;
    @BindView( R.id.gallery_title)
    TextView mGalleryTitle;
    private boolean mIsPicEditSelectBar;
    @BindView(R.id.list)
    RecyclerView mRecyclerView;
    private String mStickerId;
    @BindView(R.id.top_bar)
    RelativeLayout mTopBar;
    private String mTopicName;



    class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int TYPE_CAMERA = 0;
        private int TYPE_IMAGE = 1;
        private Context mContext;
        private ArrayList<GalleryModel> mImages = new ArrayList();
        private RecyclerView.LayoutParams mParams;

        public class CameraViewHolder extends RecyclerView.ViewHolder{
            public ImageView mCamera;

            public CameraViewHolder(View view) {
                super(view);
                view.setLayoutParams(GalleryAdapter.this.mParams);
                this.mCamera = (ImageView) view.findViewById(R.id.camera);
            }
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {
            TextView mDuration;
            RectImageView mImage;
            ImageView mMask;
            ImageView mVideoTag;

            public ImageViewHolder(View view) {
                super(view);
                view.setLayoutParams(GalleryAdapter.this.mParams);
                this.mImage = (RectImageView) view.findViewById(R.id.image);
                this.mMask = (ImageView) view.findViewById(R.id.mask);
                this.mVideoTag = (ImageView) view.findViewById(R.id.video_tag);
                this.mDuration = (TextView) view.findViewById(R.id.duration);
            }
        }

        public GalleryAdapter(Context context, int i) {
            this.mContext = context;
            int i2 = this.mContext.getResources().getDisplayMetrics().widthPixels;
            this.mParams = new RecyclerView.LayoutParams(i2 / i, i2 / i);
        }

        public void setImages(List<GalleryModel> list) {
            this.mImages.clear();
            if (list != null) {
                this.mImages.addAll(list);
            }
            notifyDataSetChanged();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == this.TYPE_CAMERA) {
                return new CameraViewHolder(UIHelper.inflateView(this.mContext, R.layout.item_gallery_camera, viewGroup, false));
            }
            return new ImageViewHolder(UIHelper.inflateView(this.mContext,  R.layout.item_gallery_image, viewGroup, false));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (getItemViewType(i) == this.TYPE_IMAGE) {
                final ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;
                final GalleryModel galleryModel = (GalleryModel) this.mImages.get(i);
                if (galleryModel != null) {
                    String coverPath = galleryModel.getCoverPath();
                    if (TextUtils.isEmpty(coverPath)) {
                        coverPath = galleryModel.getFilePath();
                    }
                    if (galleryModel.isVideo()) {
                        imageViewHolder.mVideoTag.setVisibility(View.VISIBLE);
                        imageViewHolder.mMask.setVisibility(View.VISIBLE);
                        imageViewHolder.mDuration.setVisibility(View.VISIBLE);
                        imageViewHolder.mDuration.setText(galleryModel.getDuration());
                        Glide.with(viewHolder.itemView).load(BitmapUtil.Scheme.FILE.wrap(coverPath)).placeholder(R.drawable.icon_default).into(imageViewHolder.mImage);
                    } else {
                        Glide.with(viewHolder.itemView).load(BitmapUtil.Scheme.FILE.wrap(coverPath)).override(mParams.width,mParams.height).placeholder(R.drawable.icon_default).into(imageViewHolder.mImage);
                        imageViewHolder.mVideoTag.setVisibility(View.GONE);
                        imageViewHolder.mMask.setVisibility(View.GONE);
                        imageViewHolder.mDuration.setVisibility(View.GONE);
                    }
                    imageViewHolder.mImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (galleryModel.isVideo()) {
                                EventBusHelper.post("event_clear_temp_video");
                                Intent intent = new Intent(GalleryActivity.this, VideoCropActivity.class);
                                intent.putExtra("key_video", galleryModel.getFilePath());
                                intent.putExtra("KEY_TOPIC_NAME", GalleryActivity.this.mTopicName);
                                GalleryActivity.this.startActivity(intent);
                                GalleryActivity.this.finish();
                            }else{
                                File file = new File(galleryModel.getFilePath());
                                if (file.exists() && file.canRead()) {
                                    startActivity(new Intent(GalleryActivity.this, PictureEditActivity.class).putExtra(PICK_PICTURE, Uri.fromFile(file)));
                                    GalleryActivity.this.finish();
                                }
                            }

                        }
                    });
                }
            }else{
                ((CameraViewHolder) viewHolder).mCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

        }

        public int getItemCount() {
            return this.mImages.size();
        }

        public int getItemViewType(int i) {
            return this.TYPE_IMAGE;
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind((Activity) this);
        initData();
        initView();
        loadAllImage();
    }

    protected boolean isSupportSwipeBack() {
        return false;
    }

    private void initData() {
        Intent intent = getIntent();
        this.mStickerId = intent.getStringExtra("KEY_STICKER_ID");
        this.mFilterId = intent.getStringExtra("KEY_FILTER_ID");
        this.mTopicName = intent.getStringExtra("KEY_TOPIC_NAME");
        this.mIsPicEditSelectBar = intent.getBooleanExtra("key_no_select_bar", false);
    }

    private void initView() {
        this.mAdapter = new GalleryAdapter(this, 3);
        this.mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mGalleryTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGalleryTitle.isSelected()) {
                    hideFolderLayout();
                } else {
                    showFolderLayout();
                }
            }
        });
        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadAllImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String string;
                List arrayList = new ArrayList();
                List arrayList2 = new ArrayList();
                Cursor query = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "date_modified"}, "mime_type=? or mime_type=?", new String[]{"image/jpeg", "image/png"}, "date_modified DESC");
                if (query != null) {
                    int columnIndex = query.getColumnIndex("_data");
                    int columnIndex2 = query.getColumnIndex("date_modified");
                    while (query.moveToNext() && !isFinishing()) {
                        string = query.getString(columnIndex);
                        long j = query.getLong(columnIndex2);
                        if (string != null) {
                            File file = new File(string);
                            if (file.exists() && file.canRead() && file.isFile() && file.length() > 10240) {
                                GalleryModel galleryModel = new GalleryModel(string, string);
                                galleryModel.setModifyTime(j);
                                arrayList.add(galleryModel);
                            }
                        }
                    }
                    query.close();
                }
                Cursor query2 = getContentResolver().query(Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "duration", "_id", "date_modified", "width", "height"}, "(mime_type= ?)", new String[]{"video/mp4"}, "date_modified DESC");
                String[] strArr = new String[]{"_data"};
                if (query2 != null) {
                    int columnIndex3 = query2.getColumnIndex("_data");
                    int columnIndex4 = query2.getColumnIndex("_id");
                    int columnIndex5 = query2.getColumnIndex("date_modified");
                    while (query2.moveToNext() && !isFinishing()) {
                        String string2 = query2.getString(columnIndex3);
                        long j2 = query2.getLong(columnIndex5);
                        long j3 = query2.getLong(query2.getColumnIndex("duration"));
                        if (string2 != null) {
                            File file2 = new File(string2);
                            if (file2.exists() && file2.canRead() && file2.isFile() && file2.length() > 10240 && file2.length() <= 52428800) {
                                String string3 = query2.getString(columnIndex4);
                                query = getContentResolver().query(Thumbnails.EXTERNAL_CONTENT_URI, strArr, "(video_id = ?)", new String[]{"" + string3}, null);
                                GalleryModel galleryModel2 = new GalleryModel();
                                if (query != null) {
                                    while (query.moveToNext()) {
                                        string = query.getString(query.getColumnIndex("_data"));
                                        File file3 = new File(string);
                                        if (file3.exists() && file3.canRead() && file3.isFile()) {
                                            galleryModel2.setCoverPath(string);
                                        }
                                    }
                                    query.close();
                                }
                                int i = query2.getInt(query2.getColumnIndex("width"));
                                int i2 = query2.getInt(query2.getColumnIndex("height"));
                                galleryModel2.setFilePath(string2);
                                galleryModel2.setModifyTime(j2);
                                galleryModel2.setDuration(j3);
                                galleryModel2.setVideoWidth(i);
                                galleryModel2.setVideoHeight(i2);
                                arrayList2.add(galleryModel2);
                            }
                        }
                    }
                    query2.close();
                }
                mAllImages = mergeList(arrayList, arrayList2);
                mCurrentImages.clear();
                mCurrentImages.addAll(mAllImages);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setImages(mCurrentImages);
                        loadFolder();
                    }
                });
            }
        }).start();
    }


    private void loadFolder() {
        mGalleryFolderAdapter = new GalleryFolderAdapter(this);
        GalleryFolder galleryFolder = new GalleryFolder();
        galleryFolder.setCount(mCurrentImages.size());
        galleryFolder.addImages(mCurrentImages);
        if (!(mCurrentImages.isEmpty() || mCurrentImages.get(0) == null || TextUtils.isEmpty(((GalleryModel) mCurrentImages.get(0)).getFilePath()))) {
            galleryFolder.setFirstImagePath(((GalleryModel) mCurrentImages.get(0)).getFilePath());
        }
        galleryFolder.setDir("/相机胶卷");
        mFilteredFolders.put("/相机胶卷", galleryFolder);
        mGalleryFolderAdapter.setFolder(galleryFolder);
        mFolderList.setAdapter(mGalleryFolderAdapter);
        mFolderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryFolder a = mGalleryFolderAdapter.getItem(position);
                mCurrentImages.clear();
                mCurrentImages.addAll(a.getImages());
                mAdapter.setImages(mCurrentImages);
                mGalleryFolderAdapter.setFolder(a);
                mGalleryTitle.setText(a.getName());
                hideFolderLayout();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (GalleryModel galleryModel : mAllImages) {
                    if (galleryModel != null) {
                        String absolutePath = new File(galleryModel.getFilePath()).getParentFile().getAbsolutePath();
                        if (new File(absolutePath).isDirectory()) {
                            if (mFilteredFolders.containsKey(absolutePath)) {
                                ((GalleryFolder) mFilteredFolders.get(absolutePath)).addImage(galleryModel);
                            } else {
                                GalleryFolder galleryFolder = new GalleryFolder();
                                galleryFolder.setDir(absolutePath);
                                galleryFolder.addImage(galleryModel);
                                mFilteredFolders.put(absolutePath, galleryFolder);
                            }
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGalleryFolderAdapter.setFolderList(new ArrayList(mFilteredFolders.values()));
                    }
                });
            }
        }).start();
    }


    private void hideFolderLayout() {
        mFolderList.setVisibility(View.GONE);
        mGalleryTitle.setSelected(false);
        mFolderBg.setVisibility(View.GONE);
    }

    private void showFolderLayout() {
        mFolderBg.setVisibility(View.VISIBLE);
        mFolderBg.setImageBitmap(GalleryActivity.getViewBp(mRecyclerView));
        mFolderList.setVisibility(View.VISIBLE);
        mGalleryTitle.setSelected(true);
    }

    private List<GalleryModel> mergeList(List<GalleryModel> list, List<GalleryModel> list2) {
        if (list == null || list.size() == 0) {
            return list2;
        }
        if (list2 == null || list2.size() == 0) {
            return list;
        }
        List<GalleryModel> arrayList = new ArrayList();
        arrayList.addAll(list);
        arrayList.addAll(list2);
        Collections.sort(arrayList);
        return arrayList;
    }

    public static Bitmap getViewBp(View view) {
        if (view == null) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        view.measure(MeasureSpec.makeMeasureSpec(view.getWidth(), MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(view.getHeight(), MeasureSpec.UNSPECIFIED));
        view.layout((int) view.getX(), (int) view.getY(), ((int) view.getX()) + view.getMeasuredWidth(), ((int) view.getY()) + view.getMeasuredHeight());
        Bitmap createBitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return createBitmap;
    }

    public String getPageId() {
        return "30000198";
    }
}
