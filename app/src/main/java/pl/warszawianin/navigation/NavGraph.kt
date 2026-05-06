package pl.warszawianin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.warszawianin.ui.screens.photocapture.PhotoCaptureScreen
import pl.warszawianin.ui.screens.reportdraft.ReportDraftScreen
import pl.warszawianin.ui.screens.ticketlist.TicketListScreen

object Routes {
    const val TICKET_LIST = "ticket_list"
    const val PHOTO_CAPTURE = "photo_capture"
    const val REPORT_DRAFT = "report_draft/{reportId}"

    fun reportDraft(reportId: Long) = "report_draft/$reportId"
}

@Composable
fun WarszawianinNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.TICKET_LIST
    ) {
        composable(Routes.TICKET_LIST) {
            TicketListScreen(
                onAddReport = { navController.navigate(Routes.PHOTO_CAPTURE) }
            )
        }

        composable(Routes.PHOTO_CAPTURE) {
            PhotoCaptureScreen(
                onPhotoTaken = { reportId ->
                    navController.navigate(Routes.reportDraft(reportId)) {
                        popUpTo(Routes.TICKET_LIST)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.REPORT_DRAFT) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")?.toLongOrNull() ?: 0L
            ReportDraftScreen(
                reportId = reportId,
                onBack = { navController.popBackStack() },
                onSubmitted = {
                    navController.navigate(Routes.TICKET_LIST) {
                        popUpTo(Routes.TICKET_LIST) { inclusive = true }
                    }
                }
            )
        }
    }
}
