<svelte:options runes={true} />
<script lang="ts">
	import MoodEmoji from '$components/mood-emoji/mood-emoji.svelte';

	let { chosenValue = $bindable(3) }: { chosenValue?: number } = $props();

	let options: HTMLButtonElement = $state(null);
	let button: HTMLButtonElement = $state(null);
	let desktop: boolean = $state(window.innerWidth >= 1000);
	let classVal: string = $derived(`moodRating${chosenValue}`);
	let moodText: string = $derived.by(() => {
		switch (chosenValue) {
			case 1:
				return 'Awful';
			case 2:
				return 'Meh';
			case 3:
				return 'Balanced';
			case 4:
				return 'Energized';
			case 5:
				return 'Ecstatic';
			default:
				return '';
		}
	});
	function toggleBodyScroll(): void {
		if (window.innerWidth < 1000) document.body.style.overflow = document.body.style.overflow === 'hidden' ? 'auto' : 'hidden';
	}

	function getButtonOffsets(): { top: number; right: number; bottom: number; left: number } {
		const buttonRectangle = button.getBoundingClientRect();
		return Object.freeze({
			top: buttonRectangle.top,
			right: window.innerWidth - buttonRectangle.right,
			bottom: window.innerHeight - buttonRectangle.bottom,
			left: desktop ? 0 : buttonRectangle.left
		});
	}
	let topScreenOffset = $state(40);
	function updateValue(e: Event): void {
		const input = e.target as HTMLInputElement;
		chosenValue = parseInt(input.value);
	}

	function closeOptions(e: Event): void {
		const buttonOffsets = getButtonOffsets();
		const labels = options.querySelectorAll('label');
		// Quickly move labels to top
		labels.forEach((label, i) => {
			label.style.transitionDuration = `${0.05 * i}s`;
			label.style.inset = `${topScreenOffset}px 0 0 ${buttonOffsets.left}px`;
			if (i + 1 === labels.length) {
				// After last label up top, move labels down to button location
				label.addEventListener('transitionend', () => {
					labels.forEach((label) => {
						label.style.transitionDuration = 'var(--original-transition-duration)';
						label.style.inset = 'var(--button-inset)';
					});
					// After labels are in place, "close" the options window.
					label.addEventListener('transitionend', () => {
						options.classList.remove('open');
						options.addEventListener(
							'transitionend',
							() => {
								options.classList.remove('z-open');
							},
							{ once: true }
						);
					}, { once: true });
				}, { once: true });
			}
		});
	}

	function openOptions(e: Event): void {
		toggleBodyScroll();
		const immediateLabelTransition = 'immediateLabelTransition';
		options.classList.add(immediateLabelTransition, 'initialTransition');
		const labels = options.querySelectorAll('label');
		const buttonOffsets = getButtonOffsets();
		const top = desktop ? 0 : buttonOffsets.top;
		const left = desktop ? 0 : buttonOffsets.left;
		const rest = `${0}px ${0}px ${left}px`;
		labels.forEach((label, i) => {
			label.style.setProperty('--button-inset', `${top}px ${rest}`);
		});
		options.classList.remove(immediateLabelTransition);

		options.addEventListener(
			'transitionend',
			() => {
				// Have labels move down into position in sequence, hiding behind the previous label
				labels.forEach((label, i) => {
					label.style.setProperty('inset', `${topScreenOffset}px ${rest}`);
					const spacing = 10;
					const cumulativeSpacing = spacing * i;
					const labelHeight = label.getBoundingClientRect().height;
					const heightOfPrevLabels = labelHeight * i;
					const labelTopOffset = heightOfPrevLabels + cumulativeSpacing + topScreenOffset;
					label.addEventListener(
						'transitionend',
						() => {
							label.style.transitionDuration = `${0.1 * i}s`;
							label.style.setProperty('inset', `${labelTopOffset}px ${rest}`);
							if (i === labels.length) {
								options.classList.remove('initialTransition');
							}
							label.addEventListener(
								'transitionend',
								() => {
									label.style.transitionDuration = 'var(--original-transition-duration)';
								},
								{ once: true }
							);
						},
						{ once: true }
					);
				});
			},
			{ once: true }
		);
		options.classList.add('open', 'z-open');
	}
	$effect(() => {
		function resize() {
			desktop = window.innerWidth >= 1000;
			if (desktop) {
				topScreenOffset = 0;
			}
		}
		window.addEventListener('resize', resize);
		return () => window.removeEventListener('resize', resize);
	});
</script>

<div class="mood-rating-container">
	<div class="mood-rating">
		<div class="question">How do you feel?</div>
		<div class="options-container">
			<button type="button" bind:this={options} class="not-button options initialTransition" onclick={closeOptions}>
				<input
					type="radio"
					name="moodRating"
					id="moodRating1"
					value="1"
					checked={chosenValue === 1}
					required
					class="visually-hidden"
					onchange={updateValue}
				/>
				<label for="moodRating1" class="moodRating1"><MoodEmoji moodRating={1} /> Awful</label>
				<input
					type="radio"
					name="moodRating"
					id="moodRating2"
					value="2"
					checked={chosenValue === 2}
					required
					class="visually-hidden"
					onchange={updateValue}
				/>
				<label for="moodRating2" class="moodRating2"><MoodEmoji moodRating={2} /> Meh</label>
				<input
					type="radio"
					name="moodRating"
					id="moodRating3"
					value="3"
					checked={chosenValue === 3}
					required
					class="visually-hidden"
					onchange={updateValue}
				/>
				<label for="moodRating3" class="moodRating3"><MoodEmoji moodRating={3} /> Balanced</label>
				<input
					type="radio"
					name="moodRating"
					id="moodRating4"
					value="4"
					checked={chosenValue === 4}
					required
					class="visually-hidden"
					onchange={updateValue}
				/>
				<label for="moodRating4" class="moodRating4"><MoodEmoji moodRating={4} /> Energized</label>
				<input
					type="radio"
					name="moodRating"
					id="moodRating5"
					value="5"
					checked={chosenValue === 5}
					required
					class="visually-hidden"
					onchange={updateValue}
				/>
				<label for="moodRating5" class="moodRating5"><MoodEmoji moodRating={5} /> Ecstatic</label>
			</button>
			<button
				bind:this={button}
				class="selected not-button {classVal}"
				onclick={openOptions}
				type="button"><MoodEmoji moodRating={chosenValue} /> {moodText}</button
			>
		</div>
	</div>
</div>

<style lang="scss">
	.mood-rating {
		background: transparent;
	}
	.options {
		position: fixed;
		top: 0;
		left: 0;
		width: 100vw;
		height: 100vh;
		background: #fff;
		filter: opacity(0);
		z-index: -1;
		transition: filter 0.2s ease;
		&.open {
			filter: opacity(1);
			:global(label) {
				--original-transition-duration: 0.1s;
			}
		}
		&.z-open {
			z-index: 1;
		}
		&.initialTransition {
			:global(label) {
				--original-transition-duration: 0.3s;
				transition-delay: 0.1s;
			}
		}
		&.immediateLabelTransition {
			:global(label) {
				transition: none;
			}
		}
	}
	label,
	.selected {
		display: flex;
		align-items: center;
		gap: 20px;
		--side-padding: 40px;
		padding: 10px var(--side-padding);
		width: 100%;
		max-width: 300px;
		height: fit-content;
		font: 700 1.25rem var(--ff);
		color: #fff;
		background: var(--bg);
		border: 1px solid transparent;
		border-radius: 5px;
		cursor: pointer;
	}
	label {
		position: absolute;
		width: calc(100% - (2 * var(--side-padding)) - (2 * var(--side-spacing)));
		inset: var(--button-inset);
		--original-transition-duration: 0.2s;
		transition: inset var(--original-transition-duration) linear;
	}
	:checked + label {
		z-index: 1;
	}
	.moodRating1 {
		background: var(--awful);
	}
	.moodRating2 {
		background: var(--meh);
	}
	.moodRating3 {
		background: var(--balanced);
	}
	.moodRating4 {
		background: var(--energized);
	}
	.moodRating5 {
		background: var(--ecstatic);
	}
	@media(prefers-color-scheme: dark) {
		.options{
			background: var(--dark2);
		}
		.moodRating1{
			border-color: #fff;
		}
	}
	@media (min-width: 1000px){
		.question{
			line-height: 1;
			margin-bottom: 20px;
		}
		.options-container{
			position: relative;
			display: flex;
			flex-flow: column-reverse;
			gap: 20px;
		}
		.options{
			background: transparent;
			position: relative;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			label{
				position: absolute;
				width: calc(100% - (2 * var(--side-padding)));
			}
		}
		.selected{
			position: relative;
			top: 0;
			left: 0;
		}
	}
</style>
