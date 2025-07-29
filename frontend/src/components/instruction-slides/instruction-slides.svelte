<svelte:options runes={true} />
<script lang="ts">
	import { v4 } from 'uuid';
	import Slide from './slide.svelte';

	let { id = v4(), onEnd, onlyForwards = false, slides = [], ...restProps }: { id?: string, onEnd?: ()=>void, onlyForwards?: boolean, slides?: string[]} = $props();

	let activeSlide = $state(0);
	const previousSlideDisabled = $derived(activeSlide === 0);
	let adjustedSlides = $state(slides.map((slide, i) => {
		return {
			slide,
			active: i === 0,
			hidden: i !== 0
		}
	}));

	function skip() {
		if (typeof onEnd === 'function') onEnd();
	}
	function changeSlide(currentSlide, nextSlide) {
		adjustedSlides[currentSlide].active = false;
		adjustedSlides[nextSlide].hidden = false;
		adjustedSlides = adjustedSlides;
		function afterAnim(){
			/* if (document.querySelector(`#${id} .slide.active`)) {
				setTimeout(afterAnim, 100);
			} */
			adjustedSlides[currentSlide].hidden = true;
			adjustedSlides[nextSlide].active = true;
			activeSlide = nextSlide;
		}
		setTimeout(() => {
			afterAnim();
			adjustedSlides = adjustedSlides;
		}, 300);
	}
	function previousSlide() {
		const currentSlide = activeSlide;
		const previousSlide = currentSlide - 1;

		if (previousSlide < 0) return;
		changeSlide(currentSlide, previousSlide);
	}
	function nextSlide() {
		const currentSlide = activeSlide;
		const nextSlide = activeSlide + 1;

		if (nextSlide + 1 > adjustedSlides.length) {
			if (typeof onEnd === 'function') onEnd();
			return;
		}
		changeSlide(currentSlide, nextSlide);
	}
</script>

<div class="instruction-slides" {...restProps} {id}>
	<button onclick={skip} class="skip not-button">Skip</button>
	<div class="slides">
		{#each adjustedSlides as {slide, active, hidden}}
			<Slide {active} {hidden}>
				{@html slide}
			</Slide>
		{/each}
	</div>
	<div class={onlyForwards ? 'pagination only-forwards' : 'pagination'}>
		<!-- Previous button shown if not first slide -->
		<button class="previous not-button" onclick={previousSlide} disabled={previousSlideDisabled}>
			<picture>
				<img
					loading="lazy"
					decoding="async"
					src="/icons/right-button.svg"
					width="56"
					height="56"
					alt="Previous"
				/>
			</picture>
		</button>
		<!-- Dots shown for each slide -->
		<div class="dash-container" aria-live="polite">
			{#each adjustedSlides as {active}, index}
				<div class="dash" class:active>
					<span class="sr-visible">{index + 1}</span>
				</div>
			{/each}
		</div>
		<!-- Next button shown if not last slide -->
		<button class="next not-button" onclick={nextSlide}>
			<picture>
				<source
					srcset="/icons/right-button.svg"
					media="(prefers-color-scheme: dark)"
					width="56"
					height="56"
					type="image/svg+xml"
				/>
				<img
					loading="lazy"
					decoding="async"
					src="/icons/right-button.svg"
					width="56"
					height="56"
					alt="Next"
				/>
			</picture>
		</button>
	</div>
</div>

<style lang="scss">
	.instruction-slides {
		display: flex;
		flex-flow: column;
		width: 100%;
		.skip {
			display: block;
			margin-bottom: auto;
			margin-left: auto;
			width: max-content;
			color: var(--dark1);
		}
		.slides {
			flex: 1 1 auto;
		}
		.pagination {
			display: flex;
			justify-content: space-between;
			align-items: center;
			&.only-forwards {
				.previous {
					display: none;
				}
			}
		}
		.dash-container {
			display: flex;
			gap: 4px;
		}
		.dash {
			width: 20px;
			height: 8px;
			border-radius: 5px;
			background: var(--light1);
			filter: opacity(0.3);
			transition: filter 0.2s ease;
			&.active {
				filter: opacity(1);
			}
		}
		.previous {
			img {
				transform: rotate(180deg);
			}
		}
		@media (prefers-color-scheme: dark) {
			.dash {
				background: var(--light1);
			}
			.skip {
				color: var(--light2);
			}
		}
	}
</style>
