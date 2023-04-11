package com.example.gpt.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gpt.R
import com.example.gpt.ui.theme.MediumPadding

@Composable
fun ShowLoading(
    horizontalAlignment: Alignment.Horizontal = Alignment.End,
    padding: Dp = MediumPadding
) {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(padding)
        ,
        horizontalAlignment = horizontalAlignment
    ) {
        Card(
            modifier = Modifier
                .width(200.dp)
                .padding(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            ShowCardHeader(
                text = stringResource(id = R.string.gpt)
            )
            Spacer(modifier = Modifier.height(10.dp))
            DotsLoading(
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 8.dp,
                    bottom = 6.dp,
                    end = 12.dp
                )
            )
        }
    }
}
