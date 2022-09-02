/**
 * 图形码操作Start
 * */

/* 返回一个 min~max 的随机数 */
function randomNum(min, max) {
	return Math.floor(Math.random() * (max - min) + min);
}

/* 随机RGB颜色 */
function randomRGB(min, max) {
	var r = randomNum(min, max);
	var g = randomNum(min, max);
	var b = randomNum(min, max);
	return "rgb(" + r + "," + g + "," + b + ")";

}

/* 画图函数 */
function drawimg() {
	var c = document.getElementById("imagecode");
	/* 防止报错 */
	if (c == null) {
		return null;
	}
	var width = 70;
	var height = 36;
	var ctx = c.getContext("2d");
	//验证码
	var code = "";
	var str = 'ABCEFGHJKLMNPQRSTWXY123456789';

	// 生成四个
	for (var i = 0; i < 4; i++) {
		var ch = str[randomNum(0, str.length)];
		//拼接
		code = code + ch;
		//随机字体颜色
		ctx.fillStyle = randomRGB(0, 200);
		//字体大小和字体类型
		ctx.font = randomNum(15, 20) + 'px Arial';
		//坐标原点和旋转角度
		var x = 10 + i * 16;
		var y = randomNum(25, 35);
		var deg = randomNum(-30, 30);
		//修改坐标旋转画布写文字然后再转过来
		ctx.translate(x, y);
		ctx.rotate(deg * Math.PI / 180);
		ctx.fillText(ch, 0, 0);
		ctx.rotate(-deg * Math.PI / 180);
		ctx.translate(-x, -y);
	}

	//制造干扰点
	for (var i = 0; i < 20; i++) {
		ctx.fillStyle = randomRGB(0, 255);
		ctx.beginPath();
		ctx.arc(randomNum(0, width), randomNum(0, height), 1, 0, Math.PI * 2);
		ctx.fill();
	}
	//制造干扰线
	for (var i = 0; i < 2; i++) {
		ctx.strokeStyle = randomRGB(0, 200);
		ctx.beginPath();
		ctx.moveTo(randomNum(0, width), randomNum(0, height));
		ctx.lineTo(randomNum(0, width), randomNum(0, height));
		ctx.stroke();
	}
	return code;
}

// 图形码
var verCode;

/* 初始化图形码函数 */
function imgload() {
	verCode = drawimg();
	if (verCode == null) {

	} else {
		verCode = verCode.toLocaleLowerCase();
		console.log(verCode);
	}
}

/*刷新图形码*/
function flushImgCode() {
	//重置高度画布就会被清空
	$("#imagecode").attr('height', 38);
	verCode = drawimg().toLocaleLowerCase();
	console.log(verCode);
}

function getCode() {
	return verCode;
}

imgload();
/* 监听图形码, 刷新 */
$("#imagecode").on('click', function() {
	flushImgCode();
});
/**
 * 图形码操作END
 * */
