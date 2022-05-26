package com.airasoft.banregio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.airasoft.banregio.model.Estado
import com.airasoft.banregio.model.PrestamosModel
import com.airasoft.banregio.model.TasasModel
import javax.security.auth.login.LoginException

val prestamos = arrayListOf(
    PrestamosModel("00103228", 1, "10-ene-21", 37500.00, Estado.Pendiente),
    PrestamosModel("00103228", 2, "19-ene-21", 725.18, Estado.Pendiente),
    PrestamosModel("00103228", 3, "31-ene-21", 1578.22, Estado.Pendiente),
    PrestamosModel("00103228", 4, "04-feb-21", 380.00, Estado.Pendiente),
    PrestamosModel("70099925", 1, "07-ene-21", 2175.25, Estado.Pagado),
    PrestamosModel("70099925", 2, "13-ene-21", 499.99, Estado.Pagado),
    PrestamosModel("70099925", 3, "24-ene-21", 5725.18, Estado.Pendiente),
    PrestamosModel("70099925", 4, "07-feb-21", 876.13, Estado.Pendiente),
    PrestamosModel("00298185", 1, "04-feb-21", 545.55, Estado.Pendiente),
    PrestamosModel("15000125", 1, "31-dic-20", 15220.00, Estado.Pagado)
)

val tasas = arrayListOf(
    TasasModel(1, 1, 7.00),
    TasasModel(2, 7, 6.50),
    TasasModel(8, 15, 6.00),
    TasasModel(16, 30, 5.50),
    TasasModel(31, 360, 5.00)
)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pago = obtenerPagosPendientes("15-feb-21", "00103228", 16.00, 360)

        val tvCliente = findViewById<TextView>(R.id.tvCliente)
        val tvPlazo = findViewById<TextView>(R.id.tvPlazo)
        val tvTasa = findViewById<TextView>(R.id.tvTasa)
        val tvMonto = findViewById<TextView>(R.id.tvMonto)
        val tvInteres = findViewById<TextView>(R.id.tvInteres)
        val tvIva = findViewById<TextView>(R.id.tvIva)
        val tvPago = findViewById<TextView>(R.id.tvPago)

        tvCliente.text = "Cliente: " + "00103228"
        tvPlazo.text = "Plazo: " + ""
        tvTasa.text = "Tasa: " + ""
        tvMonto.text = "Monto: " + ""
        tvInteres.text = "Interes: " + ""
        tvIva.text = "Iva: " + ""
        tvPago.text = "Pago: " + pago.toString()

    }

    // Se reciben los parametros necesarios para poder calcular el monto
    fun obtenerPagosPendientes(fechaActual: String, cliente: String, tasaIva: Double, diasComerciales: Int): Double {
        var interesRounded: Double
        var ivaRounded: Double
        var plazo = 0
        var interes = 0.0
        var tasa = 0.0
        var iva = 0.0
        var pago = 0.0

        for (i in 0 until prestamos.size) {
            if (cliente == prestamos[i].cliente) {
                // fecha actual - fecha préstamo
                plazo = calcularPlazo(prestamos[i].fecha, fechaActual)

                for (i in 0 until tasas.size) {
                    if (plazo >= tasas[i].plazoMin && plazo <= tasas[i].plazoMax){
                        tasa = tasas[i].tasaInteres
                    }
                }

                // monto préstamo * plazo * tasa interés / dias comerciales
                interes = (prestamos[i].monto * plazo * tasa) / diasComerciales
                interesRounded = String.format("%.2f", interes).toDouble()

                // interés + tasa iva
                iva = interesRounded + tasaIva
                ivaRounded = String.format("%.2f", iva).toDouble()

                // monto préstamo + interés + iva
                pago = prestamos[i].monto + interesRounded + ivaRounded
            }
        }
        return pago
    }

    fun calcularPlazo(fechaPrestamo: String, fechaActual: String): Int {
        var plazo = 0
        var diaPrestamo = (fechaPrestamo.split('-')[0]).toInt()
        var diaActual = (fechaActual.split('-')[0]).toInt()
        var mesPrestamo = (fechaPrestamo.split('-')[1])
        var mesActual = (fechaActual.split('-')[1])
        var anioPrestamo = (fechaPrestamo.split('-')[2]).toInt()
        var anioActual = (fechaActual.split('-')[2]).toInt()

        if (diaPrestamo != diaActual) {
            plazo += diaActual - diaPrestamo //10
        }

        if (mesPrestamo != mesActual) {
            plazo += 30 * (evaluarMes(mesPrestamo, mesActual)) // 30
        }

        if (anioPrestamo != anioActual) {
            plazo += 365 * (anioActual - anioPrestamo)
        }

        return plazo
    }

    fun evaluarMes(mPrestamo: String, mActual: String): Int {
        var mesActualInt = 0
        var mesPrestamoInt = 0

        when(mActual) {
            "ene" -> { mesActualInt = 1 }
            "feb" -> { mesActualInt = 2 }
            "dic" -> { mesActualInt = 12 }
        }

        when(mPrestamo) {
            "ene" -> { mesPrestamoInt = 1 }
            "feb" -> { mesPrestamoInt = 2 }
            "dic" -> { mesPrestamoInt = 12 }
        }

        return mesActualInt - mesPrestamoInt
    }
}