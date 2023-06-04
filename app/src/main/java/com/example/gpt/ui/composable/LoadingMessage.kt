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
import com.example.gpt.ui.theme.EIGHT_DP
import com.example.gpt.ui.theme.FOUR_DP
import com.example.gpt.ui.theme.SIX_DP
import com.example.gpt.ui.theme.TEN_DP
import com.example.gpt.ui.theme.TWELVE_DP

@Composable
fun ShowLoading(
    horizontalAlignment: Alignment.Horizontal = Alignment.End,
    padding: Dp = TWELVE_DP
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
                .padding(FOUR_DP),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = EIGHT_DP
            ),
            shape = RoundedCornerShape(TWELVE_DP)
        ) {
            ShowCardHeader(
                text = stringResource(id = R.string.gpt)
            )
            Spacer(modifier = Modifier.height(TEN_DP))
            DotsLoading(
                modifier = Modifier.padding(
                    start = TWELVE_DP,
                    top = EIGHT_DP,
                    bottom = SIX_DP,
                    end = TWELVE_DP
                )
            )
        }
    }
}
