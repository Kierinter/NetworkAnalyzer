package com.kks3.networkanalyzer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun buttonExample() {
    Button(
        onClick = {
            Log.d("buttonExample","click the button")
        },
    ) {
        Text("click me ")
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun modifierExample() {

    Text(
        text = stringResource(id = R.string.hello),
        style = TextStyle(background = Color.Red, fontSize = 24.sp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Green)


    )
    
}
@Composable
fun TextExample(modifier: Modifier = Modifier) {
//    SelectionContainer(){
//        Text(
//            stringResource(id = R.string.hello),
//            modifier = modifier.fillMaxSize(),
//            color = Color.Red,
//            fontSize = 24.sp,
//            maxLines = 1,
//            style = TextStyle(
//                background = Color.Yellow,
//                shadow = Shadow(
//                    color = Color.Green,
//                    offset = Offset(5f,5f)
//                ),
//                textIndent = TextIndent(20.sp)
//            )
//        )
//    }
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(Color.Red)){
            append("锄禾日当午，\n")
        }
        withStyle(style = SpanStyle(Color.Green)) {
            append("汗滴禾下土。\n")
        }
        withStyle(style = SpanStyle(Color.Blue)) {
            append("谁知盘中餐，\n")
        }
        withStyle(style = SpanStyle(Color.Yellow)) {
            append("粒粒皆辛苦。")
        }
    }
    ClickableText( text = annotatedText,
        onClick = { offset ->
        Log.d("ClickableText", "${offset}")},
        modifier = Modifier.fillMaxSize())
}
@Composable
fun userNotice(){
    val annotatedString = buildAnnotatedString {
        append("点击登录代表您知悉和同意")

        //往字符串中添加一个注解，直到遇到 pop() 。tag 为注解标识，annotation 为传递内容
        pushStringAnnotation("protocol", annotation = "https://docs.bughub.icu/compose")
        withStyle(style = SpanStyle(Color.Blue)) {
            append("用户协议")
        }
        pop()

        append("和")

        pushStringAnnotation("privacy", annotation = "https://randywei.gitee.com")
        withStyle(style = SpanStyle(Color.Blue)) {
            append("隐私政策")
        }
        pop()
    }

    ClickableText(
        annotatedString, onClick = { offset ->
            //从字符串中查找注解
            annotatedString.getStringAnnotations("protocol", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    Log.d("TextSample", "点击了用户协议：${annotation.item}")
                }

            annotatedString.getStringAnnotations("privacy", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    Log.d("TextSample", "点击了隐私政策：${annotation.item}")
                }
        }
    )
}
@Composable
fun clickText(modifier: Modifier = Modifier) {
    ClickableText(
        buildAnnotatedString {
            append("Click ")
        }, onClick = {
            offset -> Log.d("ClickableText", "${offset}")
        }
    )

}
@Composable
//TODO: 绘制移动网络界面
fun Mobile(modifier: Modifier = Modifier) {
    TextExample()
//    Text(text = stringResource(R.string.app_name))

}



