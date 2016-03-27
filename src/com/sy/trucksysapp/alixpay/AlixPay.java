package com.sy.trucksysapp.alixpay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import com.alipay.sdk.app.PayTask;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.user.ShoplistActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

/***
 * 支付宝支付
 * @author Administrator
 *
 */
public class AlixPay {
	// 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String PARTNER = "2088911794510083";
	// 商户收款的支付宝账号
	public static final String SELLER = "kct123@vip.163.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMUXCRMqRX7X4DxxTPtFUatMyJji2xLxll/WK+l/dFueIHv9gDUDikDo2iSl13b2gPJ+yHqJLdMzinZ93Lfo/Hn8tAxT5KIlc1NYa2GaA+Dfs3+h9qbdzDR66GSTVH6ffKAVcCU1HoiyJAPhwhm1vs+3q9vZkmXR1mG79Fevhu/fAgMBAAECgYEAs1jL7EG6LbvaFIZxq/GfaMcdf2poOmvXD+y/1uIhi0FwhAPhx9wp0flSkKjNUjbsjj5qxn7QUXGEXWEZGc4M+IXYeJT2VCyiAAdoDUkzBqVw2tp9hSAB/AJgYARVgqjmKVIl28dPQVSQLjjdsXpjU9APYfC+Tattofc9YzesFuECQQD4x5viLeJ7z7E8wyTpe/5CzFWaBIXt6IyB5FA3tc7k7IfudRVvVqOwo9R5FJFB7gtnKxdDoK5Lkr4bFJFa0XfRAkEAys9hawjvV1TX5J3b2dQ0ZOupXuILuZHdOcKX+caI9ujW4aL9aYlFVEGbqRFaPwEHMkX+PVWktxz1+GddQn+IrwJARt4txL+HbfebKJD6edyNcJ/enrI9KKl/JR1R0Jzk5AeRLVeDFKVcmdwBkcBPJLXX37rBtM0X+YVCzFoOkg1rkQJBAK1vrYKoMG3Bq6LhcHqW43uOoNf4fUhjuglEJU1AAC8OrXxmM0pGTtigBqsNoNySV4Ihoyr2i5bgmGSmmHVetRcCQQDlNkIonS/e8h9FgL/MIhZYw3MKLC+G4Asmlt7SCVs1Yy9EooUQG1rnIemkh9LqrHPuGzAt7tPS+1NWEZxCyB2w";
	// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private Handler mHandler;
	private Context context;

	public AlixPay(Context context) {
		this.context = context;
		Init();
	}

	public void Init() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SDK_PAY_FLAG: {
					PayResult payResult = new PayResult((String) msg.obj);
					// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
					String resultInfo = payResult.getResult();
					String resultStatus = payResult.getResultStatus();
					String resultMemo = payResult.getMemo();
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT)
								.show();
						((Activity) context).finish();
						context.startActivity(new Intent(context,
								ShoplistActivity.class).putExtra("currentPage", 2));
					} else {
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(context, "支付结果确认中,请勿重复付款！",Toast.LENGTH_SHORT).show();
							((Activity) context).finish();
						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							Toast.makeText(context, resultMemo, Toast.LENGTH_SHORT)
									.show();
						}
					}
					break;
				}
				case SDK_CHECK_FLAG: {
					Toast.makeText(context, "检查结果为：" + msg.obj,
							Toast.LENGTH_SHORT).show();
					break;
				}
				default:
					break;
				}
			};
		};
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(String name, String description, Double price,String ordernumber) {
		// 订单
		String orderInfo = getOrderInfo(name, description, price,ordernumber);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask((Activity) context);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask((Activity) context);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};
		
		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, Double price,String ordernumber) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";
		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + ordernumber + "\"";
		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";
		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";
		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";
		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + SystemApplication.getInstance().getBaseurl()
				+ "TruckService/ReceiveNotify"
				+ "\"";
		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
//		orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
