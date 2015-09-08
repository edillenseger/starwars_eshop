package manufacture.ws.WS;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import manufacture.entity.cart.Cart;
import manufacture.entity.cart.CartProduct;
import manufacture.entity.cart.Delivery;
import manufacture.entity.cart.PaymentType;
import manufacture.entity.product.Product;
import manufacture.entity.user.User;
import manufacture.ifacade.cart.IGestionCart;
import manufacture.ifacade.catalog.ICatalog;
import manufacture.ifacade.user.IConnection;
import manufacture.ws.DTO.DevisRequestDTO;
import manufacture.ws.DTO.DevisResponseDTO;
import manufacture.ws.DTO.OrderRequestDTO;
import manufacture.ws.DTO.OrderResponseDTO;

@Transactional
@Component
@WebService(endpointInterface="manufacture.ws.WS.IOrder")
public class OrderImpl implements IOrder {
	
	private static Logger log = Logger.getLogger(OrderImpl.class);
	
	private Cart cartForWS ;
	private IGestionCart proxyCart;
	private ICatalog proxyCatalog;
	private IConnection proxyConnection;

	private double getTotal() {
		return 0 ;	
	}
	
	private CartProduct convertDevisResponseDTOToCartProduct(DevisResponseDTO devisResponseProduct){
		
		List<Product> listeProduct = proxyCatalog.getAllProductConstructorByProductRef(devisResponseProduct.getIdProductRef());
		CartProduct cartProduct = new CartProduct();
		
		//Cas 1 : la reference produit demandee n'est pas enregistree dans la base de donnees
		if(!listeProduct.isEmpty())
		{			
			for (Product product : listeProduct) 
			{
				boolean isTheSame = true ;
				
				if (product.getColor().getIdColor() != devisResponseProduct.getIdColor()) 
				{
					isTheSame = false ;
				}
				
				if (product.getMaterial().getIdMaterial() != devisResponseProduct.getIdMaterial())
				{
					isTheSame = false ;
				}
				
				if (product.getConstructor().getIdConstructor() != devisResponseProduct.getIdConstructor())
				{
					isTheSame = false ;
				}
				
				if (isTheSame)
				{
					cartProduct.setProduct(product);
					cartProduct.setQuantity(devisResponseProduct.getQuantity());
				}
			}
		}
		
		return cartProduct ;
	}
	
	private Cart convertOrderRequestToCart(OrderRequestDTO orderRequest) {
		
		Cart cart = new Cart();
		User user = proxyConnection.getUserByEmail(orderRequest.getEmailUser());
		Delivery delivery = new Delivery();
		PaymentType paymentType = new PaymentType();
		
		for (DevisResponseDTO devisResponse : orderRequest.getDevisResponse()) {
			CartProduct cp = convertDevisResponseDTOToCartProduct(devisResponse) ;
			cp.setCart(cart);
			cart.addCartProduct(cp);
		}
		cart.setUser(user);
		cart.setDateCommande(new Date());
		cart.setIsValidated(true);
		cart.setAddressBilling(user.getAddresses().get(0));
		cart.setAddressDelivery(user.getAddresses().get(0));
		
		delivery.setIdDelivery(1);
		cart.setDelivery(delivery);
		
		paymentType.setIdPayment(2);
		cart.setPaymentType(paymentType);
		
		
		cart.setDatePayment(new Date()); // a calculer une date differer
		
		return cart ;
	}
	
	@Override
	public OrderResponseDTO toOrder(OrderRequestDTO orderRequest) {
//		proxyCart.
		// TODO Auto-generated method stub
		return null;
	}
	
	public Cart getCartForWS() {
		return cartForWS;
	}
	public void setCartForWS(Cart cartForWS) {
		this.cartForWS = cartForWS;
	}
	public IGestionCart getProxyCart() {
		return proxyCart;
	}
	
	@Autowired
	public void setProxyCart(IGestionCart proxyCart) {
		this.proxyCart = proxyCart;
	}

	public ICatalog getProxyCatalog() {
		return proxyCatalog;
	}

	@Autowired
	public void setProxyCatalog(ICatalog proxyCatalog) {
		this.proxyCatalog = proxyCatalog;
	}

	public IConnection getProxyConnection() {
		return proxyConnection;
	}

	@Autowired
	public void setProxyConnection(IConnection proxyConnection) {
		this.proxyConnection = proxyConnection;
	}

}
