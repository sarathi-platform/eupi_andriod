package com.nrlm.baselinesurvey.ui.common_components.common_profile


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.ui.common_components.ToolbarComponent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun CommonProfileScreen(
    title:String,
    userDetailList: List<Pair<String, String>>,
    onBackClick:()->Unit
){
    Scaffold(
        backgroundColor = white,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ToolbarComponent(
                title = title,
                modifier = Modifier
            ) {
                onBackClick()

            }
        },){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = it.calculateTopPadding() + 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            LazyColumn {
                itemsIndexed(items = userDetailList) { index, item ->
                    Text(
                        text = buildAnnotatedString
                        {
                            withStyle(
                                style = SpanStyle(
                                    color = textColorDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = NotoSans
                                )
                            ) {
                                append("${item.first}: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = textColorDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = NotoSans
                                )
                            ) {
                                append("\n${item.second}")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(dimen_10_dp))
                }
            }



        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommonProfileScreenPreview(){
    val list = arrayListOf<Pair<String, String>>()
    list.add(Pair("Name", "Naren"))
    list.add(Pair("Email", "naren@gamil.com"))
    list.add(Pair("Name", "Naren"))
    CommonProfileScreen(title = "Profile", list, onBackClick = {})
}


