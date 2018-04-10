package game2D;

public class BossSprite extends Sprite{
	
	
	private int health;

	public BossSprite(Animation anim) {
		super(anim);
		setHealth(25);
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int h) {
		health = h;
	}

}
