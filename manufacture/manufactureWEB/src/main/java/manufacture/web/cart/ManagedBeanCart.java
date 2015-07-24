package manufacture.web.cart;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import manufacture.entity.cart.CartProduct;
import manufacture.entity.product.ConstructorProduct;
import manufacture.entity.product.Product;
import manufacture.ifacade.cart.ICartSpecificCustomer;
import manufacture.ifacade.catalog.ICatalog;
//import manufacture.web.catalogBean.ManagedBeanCatalog.Produit;
import manufacture.web.user.UserBean;
import manufacture.web.util.ClassPathLoader;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//@Component
//@Scope(value="session")

@ManagedBean(name="mbCart")
@SessionScoped
public class ManagedBeanCart {
	
	private Logger log = Logger.getLogger(ManagedBeanCart.class);

	private BeanFactory bf = ClassPathLoader.getFacadeBeanFactory();
	private ICartSpecificCustomer proxyCart = (ICartSpecificCustomer) bf.getBean(ICartSpecificCustomer.class);
	private ICatalog proxyCatalog = (ICatalog) bf.getBean(ICatalog.class);
	
	private int idSelectedProduct ;
	private int quantity ;
	
	private List<ConstructorProduct> listeProductBrute;

	private int productStock;
	
	private List<CartProduct> panier = new ArrayList<CartProduct>();
	
	@PostConstruct
	void init()
	{
		listeProductBrute = proxyCatalog.getAllConstructorProduct();
	}
	
	public void addProductToCart(int idProductToAdd) {
		CartProduct cartProduct = new CartProduct();
		Product productToAdd = getProductFromLocalListeById(idProductToAdd);
		int cartProductQuantity = cartProduct.getQuantity();
		boolean isNewProductInCart = true;
		log.info(" ===<<< ID produit ajout� au panier = "+idProductToAdd+">>>=== ");
		log.info(" ===<<< Nom produit ajout� au panier = "+productToAdd.getProductRef().getProductName()+">>>=== ");
		log.info(" ===<<< Quantit� produit ajout� au panier = "+productToAdd.getProductRef().getProductName()+">>>=== ");
		if (quantity > 0) {
			for (CartProduct cp : panier) {
				if (cp.getProduct().getIdProduct() == productToAdd.getIdProduct()) {
					isNewProductInCart = false;
					cartProductQuantity = cp.getQuantity();
					cartProductQuantity += quantity ;
					if (cartProductQuantity < cp.getProduct().getStock()) {
						cp.setQuantity(cartProductQuantity);
//						proxyCart.updateQuantityProduct(cp.getIdCartProduct(), cartProductQuantity + quantity);
					} else {
						cp.setQuantity(cp.getProduct().getStock());
					}
					break;
				}
			}
			if (isNewProductInCart) {
				cartProduct.setProduct(productToAdd);
				cartProduct.setQuantity(quantity);
				panier.add(cartProduct);
				// proxyCart.addProductToCart(cartProduct);
			}
		}
	}
	
	public void getStockByProductId(){
		for (Product product : listeProductBrute) {
			if (product.getIdProduct()== idSelectedProduct) {
				productStock = product.getStock();
				break ;
			}
		}
	}
	
	public Product getProductFromLocalListeById(int idProduct){
		Product result = new Product();
		for (Product product : listeProductBrute) {
			if (product.getIdProduct()== idProduct) {
				result = product;
				break ;
			}
		}
		return result ;
	}
	
	public List<ConstructorProduct> getListeProductBrute() {
		return listeProductBrute;
	}

	public void setListeProductBrute(List<ConstructorProduct> listeProductBrute) {
		this.listeProductBrute = listeProductBrute;
	}
	
	

	public int getIdSelectedProduct() {
		return idSelectedProduct;
	}

	public void setIdSelectedProduct(int idSelectedProduct) {
		this.idSelectedProduct = idSelectedProduct;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getProductStock() {
		return productStock;
	}

	public void setProductStock(int productStock) {
		this.productStock = productStock;
	}



	public List<CartProduct> getPanier() {
		return panier;
	}

	public void setPanier(List<CartProduct> panier) {
		this.panier = panier;
	}



	@Autowired
	private UserBean userBean;
	
}
