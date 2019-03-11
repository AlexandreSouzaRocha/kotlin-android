package br.com.livroandroid.carros.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import br.com.livroandroid.carros.R
import br.com.livroandroid.carros.domain.Carro
import br.com.livroandroid.carros.domain.CarroService
import br.com.livroandroid.carros.domain.FavoritosService
import br.com.livroandroid.carros.domain.event.FavoritoEvent
import br.com.livroandroid.carros.domain.event.SaveCarroEvent
import br.com.livroandroid.carros.extensions.loadUrl
import br.com.livroandroid.carros.extensions.setupToolbar
import br.com.livroandroid.carros.extensions.toast
import br.com.livroandroid.carros.fragments.MapaFragment
import com.google.android.gms.maps.MapFragment
import kotlinx.android.synthetic.main.activity_carro.*
import kotlinx.android.synthetic.main.activity_carro_contents.*
import kotlinx.android.synthetic.main.fragment_mapa.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class CarroActivity : BaseActivity() {
    val carro by lazy { intent.getParcelableExtra<Carro>("carro") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carro)
        // Seta o nome do carro como título da Toolbar
        setupToolbar(R.id.toolbar, carro.nome, true)
        // Atualiza os dados do carro na tela
        initViews()
        // Variável gerada automaticamente pelo Kotlin Extensions
        fab.setOnClickListener { onClickFavoritar(carro) }
    }

    // Adicona ou remove carros dos favoritos
    fun onClickFavoritar(carro: Carro) {
        doAsync {
            val favoritado = FavoritosService.favoritar(carro)
            uiThread {
                // Alerta de Sucesso
                toast(if (favoritado) R.string.msg_carro_favoritado
                else R.string.msg_carro_desfavoritado)

                // Atualiza a cor do botão FAB
                setFavoriteColor(favoritado)
                EventBus.getDefault().post(FavoritoEvent(carro))
            }
        }

    }

    fun initViews() {
        // Variáveis geradas automaticamente pelo Kotlin Extensions
        tDesc.text = carro.desc
        appBarImg.loadUrl(carro.urlFoto)
        // Foto do carro (pequena com transparência)
        img.loadUrl(carro.urlFoto)
        // Reproduz o vídeo
        imgPlayVideo.setOnClickListener {
            val url = carro.urlVideo
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(url), "video/*")
            startActivity(intent)
        }
        // Adicona o fragment do mapa
        val mapaFragment = MapaFragment()
        mapaFragment.arguments = intent.extras
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mapaFragment, mapaFragment)
                .commit()

    }

    override fun onResume() {
        super.onResume()
        taksUpdateFavoritoColor()
    }

    // Busca no banco se o carro está favoritado e atualiza a cor do FAB
    private fun taksUpdateFavoritoColor() {
        doAsync {
            val color = FavoritosService.isFavorito(carro)
            uiThread {
                setFavoriteColor(color)
            }
        }
    }

    // Desenha a cor do FAB conforme está favoritado ou não.
    fun setFavoriteColor(favorito: Boolean) {
        // Troca a cor conforme o status do favoritos
        val fundo = ContextCompat.getColor(this, if (favorito) R.color.white else R.color.red)
        val cor = ContextCompat.getColor(this, if (favorito) R.color.red else R.color.white)
        fab.backgroundTintList = ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(fundo))
        fab.setColorFilter(cor)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_carro, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_editar -> {
                startActivity<CarroFormActivity>("carro" to carro)
                finish()
            }
            R.id.action_deletar -> {
                alert(R.string.msg_confirma_excluir_carro, R.string.app_name) {
                    positiveButton(R.string.sim) {
                        // Confirmou o excluir
                        taskExcluir()
                    }
                    negativeButton(R.string.nao) {
                        // Não confirmou o excluir
                    }
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Exclui um carro do servidor
    fun taskExcluir() {
        doAsync {
            val response = CarroService.delete(carro)
            uiThread {
                toast(response!!.msg)
                finish()
                EventBus.getDefault().post(SaveCarroEvent(carro))
            }
        }
    }
}
