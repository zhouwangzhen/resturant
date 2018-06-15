package cn.kuwo.player.util;

import android.content.Context;

import java.util.List;
import java.util.logging.Logger;

import cn.kuwo.player.bean.CouponBean;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.ProgressBean;
import cn.kuwo.player.bean.RuleBean;
import cn.kuwo.player.bean.TypeBean;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmHelper {
    public static final String DB_NAME = "myRealm.realm";
    private Realm mRealm;


    public RealmHelper(Context context) {

        mRealm = Realm.getDefaultInstance();
    }

    /**
     * add （增）
     */

    public void addProduct(final ProductBean productBean) {
        mRealm.beginTransaction();
        mRealm.copyToRealm(productBean);
        mRealm.commitTransaction();

    }

    public void addType(final TypeBean typeBean) {
        mRealm.beginTransaction();
        mRealm.copyToRealm(typeBean);
        mRealm.commitTransaction();
    }

    public void addProgress(ProgressBean progressBean) {
        mRealm.beginTransaction();
        mRealm.copyToRealm(progressBean);
        mRealm.commitTransaction();
    }
    public void addCoupon(CouponBean couponBran) {
        mRealm.beginTransaction();
        mRealm.copyToRealm(couponBran);
        mRealm.commitTransaction();
    }
    public void addRule(final RuleBean ruleBean){
        mRealm.beginTransaction();
        mRealm.copyToRealm(ruleBean);
        mRealm.commitTransaction();
    }
    /**
     * query （查）
     */

    public List<ProductBean> queryAllProduct() {
        RealmResults<ProductBean> all = mRealm.where(ProductBean.class).findAll().sort("serial", Sort.ASCENDING);
        return mRealm.copyFromRealm(all);
    }
    public List<ProductBean> queryActiveAllProduct() {
        RealmResults<ProductBean> all = mRealm.where(ProductBean.class).equalTo("active",1).findAll().sort("serial", Sort.ASCENDING);
        return mRealm.copyFromRealm(all);
    }
    public List<TypeBean> queryAllType() {
        RealmResults<TypeBean> typeBean = mRealm.where(TypeBean.class).findAll();
        return mRealm.copyFromRealm(typeBean);
    }
    public List<ProgressBean> queryAllProgress() {
        RealmResults<ProgressBean> progressBean = mRealm.where(ProgressBean.class).findAll();
        return mRealm.copyFromRealm(progressBean);
    }
    public List<CouponBean> queryCouponActive() {
        RealmResults<CouponBean> couponBean = mRealm.where(CouponBean.class).equalTo("active",1).findAll();
        return mRealm.copyFromRealm(couponBean);
    }
    public TypeBean queryTypeByType(int type){
        TypeBean typeBean = mRealm.where(TypeBean.class).equalTo("number", type).findFirst();
        return typeBean;
    }
    public List<ProductBean> queryStoreCommodity(int type){
        RealmResults<ProductBean> productBeans=mRealm.where(ProductBean.class).equalTo("type",type).equalTo("active",1).findAll();
        return mRealm.copyFromRealm(productBeans);
    }
    public List<TypeBean> queryCommodityTypes(){
        RealmResults<TypeBean> typeBeans=mRealm.where(TypeBean.class).findAll();
        return mRealm.copyFromRealm(typeBeans);
    }
    public ProductBean queryCommodityById(String id){
        try {
            ProductBean productBean=mRealm.where(ProductBean.class).equalTo("objectId",id).findFirst();
            return mRealm.copyFromRealm(productBean);
        }catch (Exception e){
            com.orhanobut.logger.Logger.d(id);
            e.printStackTrace();
        }
        return null;

    }
    public List<ProductBean> queryOtherType(int index) {
        RealmResults<ProductBean> productBeen = mRealm.where(ProductBean.class).equalTo("store",1).equalTo("combo", index).findAll();
        return mRealm.copyFromRealm(productBeen);
    }
    public List<ProductBean>  queryCommodityBySerial(String serial) {
        RealmResults<ProductBean> productBeans = mRealm.where(ProductBean.class).equalTo("active",1).equalTo("serial", serial).findAll();
        return mRealm.copyFromRealm(productBeans);
    }
    public List<RuleBean> queryAllRule() {
        RealmResults<RuleBean> ruleBean = mRealm.where(RuleBean.class).findAll();
        return mRealm.copyFromRealm(ruleBean);
    }

    public Realm getRealm() {
        return mRealm;
    }

    public void close() {
        if (mRealm != null) {
            mRealm.close();
        }
    }
    /**
     * delete （删）
     */

    public boolean deleteAll(Class<? extends RealmObject> clazz) {
        try {
            mRealm.beginTransaction();
            mRealm.delete(clazz);
            mRealm.commitTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            mRealm.cancelTransaction();
            return false;
        }
    }

    public List<ProductBean> queryProductByBarcode(String code){
        RealmResults<ProductBean> productBeen = mRealm.where(ProductBean.class).equalTo("code", code).findAll();
        return mRealm.copyFromRealm(productBeen);
    }

}
