package br.com.livroandroid.carros.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.livroandroid.carros.R
import br.com.livroandroid.carros.domain.Carro
import br.com.livroandroid.carros.utils.PermissionUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapaFragment : BaseFragment(), OnMapReadyCallback {
    // Objeto que controla o Google Maps
    private var map: GoogleMap? = null
    val carro: Carro by lazy { arguments!!.getParcelable<Carro>("carro") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mapa, container, false)
        // Inicia o mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        // O método onMapReady(map) é chamado quando a incialização do mapa estiver ok
        this.map = map
        // Usa a função apply porque o carro pode ser nulo
        carro.apply {
            // Localização do usuario somente com Lat/Long = 0
            if (carro.latitude.toDouble() == 0.0) {
                // Ativa o botão para mostrar minha localização
                val ok = PermissionUtils.validate(activity!!, 1,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)

                if (ok) {
                    // Somente usa o GPS se a permissão estiver ok
                    map.isMyLocationEnabled = true
                }
            } else {
                // Cria p objeto lat/long com a coordenada da fabrica
                val location = LatLng(carro.latitude.toDouble(), carro.longitude.toDouble())
                // Posiciona o mapa na coordenada da fábrica (zoom = 13)
                val update = CameraUpdateFactory.newLatLngZoom(location, 13f)
                map.moveCamera(update)
                // Marcador no local da fábrica
                map.addMarker(MarkerOptions()
                        .title(carro.nome)
                        .snippet(carro.desc)
                        .position(location))

            }
            // Tipo do mapa: normal, satélite, terreno ou híbrido
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                // Permissão OK, podemos usar o GPS.
                map?.isMyLocationEnabled = true
                return
            }
        }
    }

}

