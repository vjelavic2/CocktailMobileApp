package rma.lv1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import rma.lv1.ui.theme.LV1Theme
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.compose.rememberNavController
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.tasks.await
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "loginRegisterScreen") {
                composable("main_screen") {
                    MainScreen(navController = navController)
                }
                composable("step_counter") {
                    StepCounter(navController = navController)
                }
                composable("loginRegisterScreen") {
                    LoginRegisterScreen(navController = navController)
                }
            }

        }
    }
}

@Composable
fun LoginRegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    fun signIn(context: Context, email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("main_screen")
                } else {
                    Toast.makeText(context, "Login failed",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { signIn(context, email, password) }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { register(context, email, password) }) {
            Text("Register")
        }
    }


}

private fun register(context: Context, email: String, password: String) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,
        password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Registered successfully",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Registration failed",
                    Toast.LENGTH_SHORT).show()
            }
        }
}



@Composable
fun UserPreview(name: String, visina: Float, tezina: Float, modifier: Modifier = Modifier) {

    val db = FirebaseFirestore.getInstance()
    var newTezina by remember { mutableFloatStateOf(0f) }
    var newVisina by remember { mutableFloatStateOf(0f) }

    var formattedBmi by remember { mutableStateOf("") }

    Text(
        text = "Pozdrav $name!",
        fontSize = 20.sp,
        lineHeight = 56.sp,
        modifier= Modifier
            .padding(top = 8.dp)
            .padding(start = 10.dp)
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "Tvoj BMI je:",
            fontSize = 55.sp,
            lineHeight = 61.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = formattedBmi,
            fontSize = 70.sp,
            lineHeight = 72.sp,
            fontWeight = FontWeight.Bold,
        )

        TextField(
            value = newTezina.toString(),
            onValueChange = { newTezina = it.toFloatOrNull() ?: 0f },
            label = { Text("Nova Tezina:") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        TextField(
            value = newVisina.toString(),
            onValueChange = { newVisina = it.toFloatOrNull() ?: 0f },
            label = { Text("Nova Visina:") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Button(onClick = {
            val docRef = db.collection("bmi").document("1jMc72CfIMl5s4lTTjdX") // Replace with your document ID
            docRef.update("Tezina", newTezina)
                .addOnSuccessListener {
                    // Update successful
                }
                .addOnFailureListener { e ->
                    // Update failed (handle error)
                    Log.e("MainActivity", "Error updating Tezina: $e")
                }

            // Update the newVisina as well
            docRef.update("Visina", newVisina)
                .addOnSuccessListener {
                    // Update successful
                }
                .addOnFailureListener { e ->
                    // Update failed (handle error)
                    Log.e("MainActivity", "Error updating Visina: $e")
                }
        }) {
            Text("Unesi promjene")
        }

        Button(onClick = {
            val docRef = db.collection("bmi").document("1jMc72CfIMl5s4lTTjdX")
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val tezina = document.getDouble("Tezina")
                        val visina = document.getDouble("Visina")

                        if (tezina != null && visina != null) {
                            val bmi = tezina /((visina/100)*(visina/100))
                            formattedBmi = String.format("%.2f", bmi)
                        } else {
                            // Handle missing Tezina or Visina values
                            Log.e("MainActivity", "Tezina or Visina is null")
                        }
                    } else {
                        // Handle document does not exist
                        Log.e("MainActivity", "Document does not exist")
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Log.e("MainActivity", "Error fetching document: $e")
                }
        }) {
            Text("Izračunaj BMI")
        }
    }
}

@Composable
fun BackgroundImage(modifier: Modifier) {

    Box (modifier){ Image(
        painter = painterResource(id = R.drawable.fitness),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alpha = 0.1F
    )
    }
}

@Composable
fun MainScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment =
    Alignment.Center) {
        BackgroundImage(modifier = Modifier.fillMaxSize())
        UserPreview(name = "Miljenko", visina = 1.91f, tezina =1000f, modifier = Modifier.fillMaxSize())
        // Button to navigate to StepCounter
        Button(
            onClick = {
                // Navigate to OtherScreen when button clicked
                navController.navigate("step_counter")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(text = "Step Counter")
        }

        Button(
            onClick = {
                navController.navigate("loginRegisterScreen")
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(text = "Log out")
        }

    }
}
@Composable
fun StepCounter(navController: NavController) {
    val sensorManager = (LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var stepCountState by remember { mutableStateOf(0) }

    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("bmi").document("1jMc72CfIMl5s4lTTjdX")

    LaunchedEffect(Unit) {
        try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val steps = snapshot.getLong("Koraci")
                if (steps != null) {
                    stepCountState = steps.toInt()
                }
            } else {
                // Handle document does not exist
                Log.e("MainActivity", "Document does not exist")
            }
        } catch (e: Exception) {
            // Handle exception
            Log.e("MainActivity", "Error fetching document: $e")
        }
    }

    DisposableEffect(Unit) {
        val sensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble())

                    val threshold = 17.0

                    if (magnitude > threshold) {
                        stepCountState++
                    }
                }
            }
        }
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            docRef.update("Koraci", stepCountState)
                .addOnSuccessListener {
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Error updating Tezina: $e")
                }
            sensorManager.unregisterListener(sensorListener)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(modifier = Modifier.fillMaxSize())
        Column {
            Text(
                text = "Step Count: $stepCountState",
                fontSize = 20.sp
            )
        }
        // Back button
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("User Info")
        }
    }
}


@Preview(showBackground = false)
@Composable
fun UserPreview() {
    LV1Theme {
        BackgroundImage(modifier = Modifier) }
}