package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntitySkull;
import net.optifine.Log;
import net.optifine.reflect.Reflector;

public class ModelAdapterHeadHumanoid extends ModelAdapter {
	public ModelAdapterHeadHumanoid() {
		super(TileEntitySkull.class, "head_humanoid", 0.0F);
	}

	public ModelBase makeModel() {
		return new ModelHumanoidHead();
	}

	public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
		if (model instanceof ModelHumanoidHead modelhumanoidhead) {
			if (modelPart.equals("head"))
				return modelhumanoidhead.skeletonHead;
			if (modelPart.equals("head2"))
				return Reflector.ModelHumanoidHead_head.exists() ? (ModelRenderer) Reflector.getFieldValue(modelhumanoidhead, Reflector.ModelHumanoidHead_head) : null;
		}
		return null;
	}

	public String[] getModelRendererNames() {
		return new String[]{"head"};
	}

	public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
		TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.INSTANCE;
		TileEntitySpecialRenderer tileentityspecialrenderer = tileentityrendererdispatcher.getSpecialRendererByClass(TileEntitySkull.class);

		if (!(tileentityspecialrenderer instanceof TileEntitySkullRenderer)) {
			return null;
		} else {
			if (tileentityspecialrenderer.getEntityClass() == null) {
				tileentityspecialrenderer = new TileEntitySkullRenderer();
				tileentityspecialrenderer.setRendererDispatcher(tileentityrendererdispatcher);
			}

			if (Reflector.TileEntitySkullRenderer_humanoidHead.exists()) {
				Reflector.setFieldValue(tileentityspecialrenderer, Reflector.TileEntitySkullRenderer_humanoidHead, modelBase);
				return tileentityspecialrenderer;
			} else {
				Log.error("Field not found: TileEntitySkullRenderer.humanoidHead");
				return null;
			}
		}
	}
}
