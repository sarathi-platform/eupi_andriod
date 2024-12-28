package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle


@Composable
fun ToolbarComponent(title:String,
                     modifier: Modifier,
    onBackIconClick:()->Unit){
    TopAppBar(
        backgroundColor = Color.White,
        modifier = modifier
    ) {
        IconButton(
            onClick = { onBackIconClick()},
            modifier = Modifier
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back Button")
        }
        Box(
            Modifier
                .fillMaxWidth()) {

                Text(
                    modifier = Modifier.padding(0.dp, 3.dp, 0.dp, 7.dp),
                    text = title,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = largeTextStyle
                )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToolbarComponentPreview(){
        ToolbarComponent(title = "Setting", modifier = Modifier) {
            
        }
}