package com.e.e_commerce_app.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.e_commerce_app.Model.PagerModel;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageSliderAdapter extends SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH> {
    Context context;
    Dialog dialogConfirmPassword;
    ImageView ivSliderImage, ivRemovePopup;
    TextView tvSlideTitle, tvSlideDesc;

    private ArrayList<PagerModel> mSliderItems;

    public ImageSliderAdapter(Context context,ArrayList<PagerModel> itm) {
        this.context=context;
        this.mSliderItems = itm;
    }

    public void renewItems(ArrayList<PagerModel> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(PagerModel sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpageritem, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        Picasso.with(context).load(APIs.IMG_URL+mSliderItems.get(position).getSliderImage()).error(R.drawable.no_image).into(viewHolder.imageViewBackground);
        viewHolder.sliderTittle.setText(mSliderItems.get(position).getSliderTitle());

        viewHolder.imageViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescPopUP(position);
            }
        });


    }

    @Override
    public int getCount() {
        //slider view
        // count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView sliderTittle;


        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.pager_image);
            sliderTittle = itemView.findViewById(R.id.sliderTitle);
            this.itemView = itemView;
        }
    }
    public void DescPopUP(int pos){
        dialogConfirmPassword =new Dialog(context);
        dialogConfirmPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogConfirmPassword.setCancelable(true);

        dialogConfirmPassword.setContentView(R.layout.slide_desc_popup);
        tvSlideTitle = dialogConfirmPassword.findViewById(R.id.descsliderTitle);
        tvSlideDesc = dialogConfirmPassword.findViewById(R.id.slide_img_desc);
        ivSliderImage = dialogConfirmPassword.findViewById(R.id.desc_image);
        ivRemovePopup = dialogConfirmPassword.findViewById(R.id.remove);

        Window window = dialogConfirmPassword.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tvSlideDesc.setText(mSliderItems.get(pos).getSliderDesc());
        tvSlideTitle.setText(mSliderItems.get(pos).getSliderTitle());
        Picasso.with(context).load(APIs.IMG_URL+mSliderItems.get(pos).getSliderImage()).error(R.drawable.no_image).into(ivSliderImage);
        ivRemovePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmPassword.dismiss();
            }
        });

        dialogConfirmPassword.show();

    }

}

