package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.dispatchCall
import model.BuildingState
import model.Direction
import model.DoorState

// Central call button panel - shared hall call buttons
@Composable
fun CentralCallButtonPanel(
    buildingState: BuildingState,
    modifier: Modifier = Modifier,
    scaleFactor: Float = 1f
) {
    val callButtonSize = (30 * scaleFactor).dp
    val floorNumberFontSize = (14 * scaleFactor).sp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Match ElevatorShaft header: titleLarge text + 8dp spacer
        Text(
            text = "CALL",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Use same 85% height as ElevatorShaft canvas for floor area alignment
        BoxWithConstraints(
            modifier = Modifier.fillMaxHeight(0.85f).fillMaxWidth()
        ) {
            val totalFloors = 6

            // Calculate effective floor area height to match elevator shaft's aspect ratio constraint
            // Shaft column has weight 1.2, call panel has weight 0.8, so shaft width ≈ our width * 1.5
            // Shaft canvas uses aspectRatio(0.3), meaning height = width / 0.3
            val estimatedShaftWidth = maxWidth * 1.5f
            val shaftHeightFromAspect = estimatedShaftWidth / 0.3f
            // Effective height is the lesser of our 85% height and the shaft's aspect-derived height
            val effectiveFloorAreaHeight = minOf(maxHeight, shaftHeightFromAspect)

            // When aspect ratio constrains the shaft to be shorter than 85%, the Canvas is centered
            // within its allocated space. Add offset to match this centering behavior.
            val verticalOffset = (maxHeight - effectiveFloorAreaHeight) / 2

            val floorHeight = effectiveFloorAreaHeight / totalFloors

            // Account for the 4dp floorGap at top of each floor in ElevatorShaft
            // The visual center of each floor is shifted down by half the gap
            val floorGapOffset = 2.dp

            for (floor in 1..totalFloors) {
                // Calculate floor center from bottom - same coordinate system as elevator shaft
                // Floor 1 is at bottom, floor 6 is at top
                val floorTop = effectiveFloorAreaHeight - (floorHeight * floor)
                val floorCenterY = floorTop + (floorHeight / 2) + floorGapOffset

                // Button group height estimate for centering
                val buttonGroupHeight = callButtonSize + floorNumberFontSize.value.dp

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = verticalOffset + floorCenterY - buttonGroupHeight / 2),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Floor number centered above the arrows
                    Text(
                        text = floor.toString(),
                        fontSize = floorNumberFontSize,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Up button (floors 1-5 only)
                        if (floor < 6) {
                            CallButton(
                                isUp = true,
                                isLit = floor in buildingState.callButtonsUp,
                                onClick = {
                                    // Check if either elevator is at this floor with doors open
                                    val elevatorAtFloor = buildingState.getAllElevators().any { elevator ->
                                        elevator.currentFloor == floor &&
                                                !elevator.isMoving &&
                                                elevator.doorState == DoorState.OPEN
                                    }
                                    if (elevatorAtFloor) return@CallButton

                                    if (floor !in buildingState.callButtonsUp) {
                                        buildingState.callButtonsUp += floor
                                        // Dispatch to best elevator
                                        val assignedElevator = dispatchCall(floor, Direction.UP, buildingState)
                                        assignedElevator.assignedCallsUp += floor
                                    }
                                },
                                buttonSize = callButtonSize
                            )
                        } else {
                            Spacer(modifier = Modifier.size(callButtonSize))
                        }

                        // Down button (floors 2-6 only)
                        if (floor > 1) {
                            CallButton(
                                isUp = false,
                                isLit = floor in buildingState.callButtonsDown,
                                onClick = {
                                    val elevatorAtFloor = buildingState.getAllElevators().any { elevator ->
                                        elevator.currentFloor == floor &&
                                                !elevator.isMoving &&
                                                elevator.doorState == DoorState.OPEN
                                    }
                                    if (elevatorAtFloor) return@CallButton

                                    if (floor !in buildingState.callButtonsDown) {
                                        buildingState.callButtonsDown += floor
                                        // Dispatch to best elevator
                                        val assignedElevator = dispatchCall(floor, Direction.DOWN, buildingState)
                                        assignedElevator.assignedCallsDown += floor
                                    }
                                },
                                buttonSize = callButtonSize
                            )
                        } else {
                            Spacer(modifier = Modifier.size(callButtonSize))
                        }
                    }
                }
            }
        }
    }
}
