package net.optifine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.src.Config;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.optifine.model.ModelUtils;

import java.util.ArrayList;
import java.util.List;

public class SmartLeaves {
	private static IBakedModel modelLeavesCullAcacia = null;
	private static IBakedModel modelLeavesCullBirch = null;
	private static IBakedModel modelLeavesCullDarkOak = null;
	private static IBakedModel modelLeavesCullJungle = null;
	private static IBakedModel modelLeavesCullOak = null;
	private static IBakedModel modelLeavesCullSpruce = null;
	private static List generalQuadsCullAcacia = null;
	private static List generalQuadsCullBirch = null;
	private static List generalQuadsCullDarkOak = null;
	private static List generalQuadsCullJungle = null;
	private static List generalQuadsCullOak = null;
	private static List generalQuadsCullSpruce = null;
	private static IBakedModel modelLeavesDoubleAcacia = null;
	private static IBakedModel modelLeavesDoubleBirch = null;
	private static IBakedModel modelLeavesDoubleDarkOak = null;
	private static IBakedModel modelLeavesDoubleJungle = null;
	private static IBakedModel modelLeavesDoubleOak = null;
	private static IBakedModel modelLeavesDoubleSpruce = null;

	public static IBakedModel getLeavesModel(IBakedModel model, IBlockState stateIn) {
		if (!Config.isTreesSmart()) {
			return model;
		} else {
			List list = model.getGeneralQuads();
			return list == generalQuadsCullAcacia ? modelLeavesDoubleAcacia : (list == generalQuadsCullBirch ? modelLeavesDoubleBirch : (list == generalQuadsCullDarkOak ? modelLeavesDoubleDarkOak : (list == generalQuadsCullJungle ? modelLeavesDoubleJungle : (list == generalQuadsCullOak ? modelLeavesDoubleOak : (list == generalQuadsCullSpruce ? modelLeavesDoubleSpruce : model)))));
		}
	}

	public static boolean isSameLeaves(IBlockState state1, IBlockState state2) {
		if (state1 == state2) {
			return true;
		} else {
			Block block = state1.getBlock();
			Block block1 = state2.getBlock();
			return block == block1 && (block instanceof BlockOldLeaf ? state1.getValue(BlockOldLeaf.VARIANT) == state2.getValue(BlockOldLeaf.VARIANT) : (block instanceof BlockNewLeaf && state1.getValue(BlockNewLeaf.VARIANT) == state2.getValue(BlockNewLeaf.VARIANT)));
		}
	}

	public static void updateLeavesModels() {
		List list = new ArrayList<>();
		modelLeavesCullAcacia = getModelCull("acacia", list);
		modelLeavesCullBirch = getModelCull("birch", list);
		modelLeavesCullDarkOak = getModelCull("dark_oak", list);
		modelLeavesCullJungle = getModelCull("jungle", list);
		modelLeavesCullOak = getModelCull("oak", list);
		modelLeavesCullSpruce = getModelCull("spruce", list);
		generalQuadsCullAcacia = getGeneralQuadsSafe(modelLeavesCullAcacia);
		generalQuadsCullBirch = getGeneralQuadsSafe(modelLeavesCullBirch);
		generalQuadsCullDarkOak = getGeneralQuadsSafe(modelLeavesCullDarkOak);
		generalQuadsCullJungle = getGeneralQuadsSafe(modelLeavesCullJungle);
		generalQuadsCullOak = getGeneralQuadsSafe(modelLeavesCullOak);
		generalQuadsCullSpruce = getGeneralQuadsSafe(modelLeavesCullSpruce);
		modelLeavesDoubleAcacia = getModelDoubleFace(modelLeavesCullAcacia);
		modelLeavesDoubleBirch = getModelDoubleFace(modelLeavesCullBirch);
		modelLeavesDoubleDarkOak = getModelDoubleFace(modelLeavesCullDarkOak);
		modelLeavesDoubleJungle = getModelDoubleFace(modelLeavesCullJungle);
		modelLeavesDoubleOak = getModelDoubleFace(modelLeavesCullOak);
		modelLeavesDoubleSpruce = getModelDoubleFace(modelLeavesCullSpruce);

		if (!list.isEmpty()) {
			Log.info("Enable face culling: " + Config.arrayToString(list.toArray()));
		}
	}

	private static List getGeneralQuadsSafe(IBakedModel model) {
		return model == null ? null : model.getGeneralQuads();
	}

	static IBakedModel getModelCull(String type, List updatedTypes) {
		ModelManager modelmanager = Config.getModelManager();

		if (modelmanager == null) {
			return null;
		} else {
			ResourceLocation resourcelocation = new ResourceLocation("blockstates/" + type + "_leaves.json");

			if (Config.getDefiningResourcePack(resourcelocation) != Minecraft.getMinecraft().getDefaultResourcePack()) {
				return null;
			} else {
				ResourceLocation resourcelocation1 = new ResourceLocation("models/block/" + type + "_leaves.json");

				if (Config.getDefiningResourcePack(resourcelocation1) != Minecraft.getMinecraft().getDefaultResourcePack()) {
					return null;
				} else {
					ModelResourceLocation modelresourcelocation = new ModelResourceLocation(type + "_leaves", "normal");
					IBakedModel ibakedmodel = modelmanager.getModel(modelresourcelocation);

					if (ibakedmodel != null && ibakedmodel != modelmanager.getMissingModel()) {
						List<BakedQuad> list = ibakedmodel.getGeneralQuads();

						if (list.isEmpty()) {
							return ibakedmodel;
						} else if (list.size() != 6) {
							return null;
						} else {
							for (BakedQuad quad : list) {
								List<BakedQuad> list1 = ibakedmodel.getFaceQuads(quad.getFace());

								if (!list1.isEmpty()) {
									return null;
								}

								list1.add(quad);
							}

							list.clear();
							updatedTypes.add(type + "_leaves");
							return ibakedmodel;
						}
					} else {
						return null;
					}
				}
			}
		}
	}

	private static IBakedModel getModelDoubleFace(IBakedModel model) {
		if (model == null) {
			return null;
		} else if (!model.getGeneralQuads().isEmpty()) {
			Log.error("SmartLeaves: Model is not cube, general quads: " + model.getGeneralQuads().size() + ", model: " + model);
			return model;
		} else {

			for (Direction enumfacing : Direction.VALUES) {
				List<BakedQuad> list = model.getFaceQuads(enumfacing);

				if (list.size() != 1) {
					Log.error("SmartLeaves: Model is not cube, side: " + enumfacing + ", quads: " + list.size() + ", model: " + model);
					return model;
				}
			}

			IBakedModel ibakedmodel = ModelUtils.duplicateModel(model);

			for (Direction direction : Direction.VALUES) {
				List<BakedQuad> list1 = ibakedmodel.getFaceQuads(direction);
				BakedQuad bakedquad = list1.getFirst();
				BakedQuad bakedquad1 = new BakedQuad(bakedquad.getVertexData().clone(), bakedquad.getTintIndex(), bakedquad.getFace(), bakedquad.getSprite());
				int[] aint = bakedquad1.getVertexData();
				int[] aint1 = aint.clone();
				int j = aint.length / 4;
				System.arraycopy(aint, 0, aint1, 3 * j, j);
				System.arraycopy(aint, j, aint1, 2 * j, j);
				System.arraycopy(aint, 2 * j, aint1, j, j);
				System.arraycopy(aint, 3 * j, aint1, 0, j);
				System.arraycopy(aint1, 0, aint, 0, aint1.length);
				list1.add(bakedquad1);
			}

			return ibakedmodel;
		}
	}
}
